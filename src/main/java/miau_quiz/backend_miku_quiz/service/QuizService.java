package miau_quiz.backend_miku_quiz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.Enums.Difficulty;
import miau_quiz.backend_miku_quiz.Enums.QuizStatus;
import miau_quiz.backend_miku_quiz.dto.QuizCreateDTO;
import miau_quiz.backend_miku_quiz.dto.QuizRatingQuestionsDTO;
import miau_quiz.backend_miku_quiz.dto.QuizUpdateDTO;
import miau_quiz.backend_miku_quiz.dto.UpdateAnswersDTO;
import miau_quiz.backend_miku_quiz.dto.UpdateQuestionDTO;
import miau_quiz.backend_miku_quiz.entity.Answer;
import miau_quiz.backend_miku_quiz.entity.Question;
import miau_quiz.backend_miku_quiz.entity.Quiz;
import miau_quiz.backend_miku_quiz.entity.Tags;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.mappers.QuizMapper;
import miau_quiz.backend_miku_quiz.repository.AnswerRepository;
import miau_quiz.backend_miku_quiz.repository.AttemptRepository;
import miau_quiz.backend_miku_quiz.repository.QuestionRepository;
import miau_quiz.backend_miku_quiz.repository.QuizRepository;
import miau_quiz.backend_miku_quiz.repository.TagsRepository;
import miau_quiz.backend_miku_quiz.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class QuizService {

	private final QuizRepository quizRepository;
	private final AttemptRepository attemptRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final TagsRepository tagsRepository;
	private final TagsService tagsService;
	private final CommonService commonService;
	private final QuizMapper quizMapper;
	private final UserRepository userRepository;

	
	@Transactional
	public Quiz createQuiz(QuizCreateDTO quizDTO, User currentUser) {
		Quiz quiz = quizMapper.toEntity(quizDTO);
		quiz.setUser(currentUser);

		if(quizDTO.tagsId() != null && !quizDTO.tagsId().isEmpty()) {
			Set<Tags> foundTags = tagsService.findTagsFindByIds(quizDTO.tagsId());
			quiz.setTags(foundTags);
		}
		
		if (quiz.getQuestions() != null) {
			for (Question question : quiz.getQuestions()) {
				question.setQuiz(quiz);

				if (question.getAnswers() != null) {
					for (Answer answer : question.getAnswers()) {
						answer.setQuestion(question);
					}
				}

			}

		}
		
		quiz.setAvgRating(5.00);
		
		return quizRepository.save(quiz);

	}

	// Method to get published quizzes with its rating and amount of questions;
	@Transactional(readOnly = true)
	public List<QuizRatingQuestionsDTO> getQuizzes(String query, String difficulty, String tagsId) {
		// 1. Cria a especificação base: buscar apenas quizzes publicados
		Specification<Quiz> spec =(root, q, cb) -> cb.equal(root.get("status"), QuizStatus.PUBLISHED);
		
		// 2. Adiciona o filtro de título (parâmetro 'q') se ele for fornecido
		if(query != null && !query.isBlank()) {
			spec = spec.and((root, q, cb) 
					-> cb.like(cb.lower(root.get("title")), "%"+ query.toLowerCase()+ "%"));
		}
		
		// 3. Adiciona o filtro de dificuldade se for fornecido e não for "All"
		if(difficulty != null && !difficulty.equalsIgnoreCase("All")) {
			try {
				spec = spec.and((root, q, cb) 
					-> cb.equal(root.get("difficulty"), Difficulty.valueOf(difficulty.toUpperCase())));
			}catch(IllegalArgumentException e) {
				
			}
		}
		
		// 4. Adiciona o filtro de tags se for fornecido
		if(tagsId != null && !tagsId.isBlank()) {
			List<UUID> tagUUIDs = Arrays.stream(tagsId.split(","))
					.map(UUID::fromString)
					.collect(Collectors.toList());
			
			if(!tagUUIDs.isEmpty()) {
				spec = spec.and((root, q, cb)->{
					q.distinct(true);
					Join<Quiz, Tags> tagsJoin = root.join("tags");
					return tagsJoin.get("tagsId").in(tagUUIDs);
				});
			}
		}
		
		// 5. Executa a query dinâmica e mapeia os resultados
		List<Quiz> quizzes = quizRepository.findAll(spec);
		
		
		List<QuizRatingQuestionsDTO> quizRatingQuestionsList = quizzes.stream()
				.map(quiz -> {
					Double rating = attemptRepository.findQuizRating(quiz.getId());
					return new QuizRatingQuestionsDTO(quiz, rating, quiz.getQuestions());
				}).collect(Collectors.toList());
		return quizRatingQuestionsList;
	}

	// The method to get quiz info and its rating by quiz ID:
	public QuizRatingQuestionsDTO getQuizById(String id) {
		Quiz quiz = commonService.checkQuizById(id);
		Double rating = attemptRepository.findQuizRating(UUID.fromString(id));
		return new QuizRatingQuestionsDTO(quiz, rating, quiz.getQuestions());
	}

	// Method to get published quizzes created by other users than the authenticated
	// one:
	public List<QuizRatingQuestionsDTO> getQuizzesOfOthersAuth(String userId) {
		List<Quiz> quizzesOfOthersNoAttempts = getQuizzesOfOthersThatUserDidntAttempt(userId);

		List<QuizRatingQuestionsDTO> QuizRatingQuestionsDTO = makeQuizRatingQuestionsListFromQuizzes(
				quizzesOfOthersNoAttempts);

		return QuizRatingQuestionsDTO;
	}

	// Method to get quizzes created by authenticated user:
	@Transactional(readOnly = true)
	public List<QuizRatingQuestionsDTO> getPersonalQuizzes(UUID userId, String statusQuiz) {
		User user = userRepository.findById(userId).orElse(null);
		
		Specification<Quiz> spec =(root, q, cb) -> cb.equal(root.get("user"), user);
		
		if(statusQuiz != null && statusQuiz.toUpperCase() != "ALL") {
			spec =  spec.and((root, q, cb) -> cb.equal(root.get("status"), QuizStatus.valueOf(statusQuiz.toUpperCase())));
		}
		
		
		List<Quiz> quizzesOfUser = quizRepository.findAll(spec);

		List<QuizRatingQuestionsDTO> quizRatingQuestionsList = quizzesOfUser.stream()
				.map(quiz -> {
					Double rating = attemptRepository.findQuizRating(quiz.getId());
					return new QuizRatingQuestionsDTO(quiz, rating, quiz.getQuestions());
				}).collect(Collectors.toList());
		return quizRatingQuestionsList;
	}
	
	public ResponseEntity<?> deleteQuizById(String quizId, User currentUser) {
		Quiz quiz = commonService.checkQuizById(quizId);
		
		if(!quiz.getUser().getUserId().equals(currentUser.getUserId())) {
			throw new AccessDeniedException("Você não tem permissão para deletar este quiz.");
		}
		
		quizRepository.deleteById(quiz.getId());

		return new ResponseEntity<>("Quiz deletado com sucesso", HttpStatus.NO_CONTENT);
	}

	public Tags findTagsByName(String tagName) {
		Optional<Tags> optionalTags = tagsRepository.findByName(tagName);
		if (!optionalTags.isPresent()) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Nao foi possivel encontrar a tag pelo nome");
		}

		Tags tag = optionalTags.get();
		return tag;
	}

	public Set<Tags> findTagsFindById(Set<String> tagsId) {
		Set<UUID> uuidSet = tagsId.stream().map(UUID::fromString).collect(Collectors.toSet());

		List<Tags> foundTags = tagsRepository.findAllById(uuidSet);

		if (foundTags.size() != tagsId.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Uma ou mais Tags com os IDs fornecidos não foram encontradas.");
		}

		return new HashSet<>(foundTags);
	}

	public List<Quiz> getQuizzesOfOthersThatUserDidntAttempt(String id) {
		List<Quiz> quizzesOfOthers = quizRepository.findPublishedQuizzesFromOtherUsers(UUID.fromString(id));

		return quizzesOfOthers.stream().filter(
				quiz -> attemptRepository.findAttemptsForTheQuizByUserId(UUID.fromString(id), quiz.getId()) == null)
				.collect(Collectors.toList());
	}

	public List<QuizRatingQuestionsDTO> makeQuizRatingQuestionsListFromQuizzes(List<Quiz> publishedQuizzes) {
		return publishedQuizzes.stream().map(this::makeQuizRatingQuestionsFromQuiz).collect(Collectors.toList());
	}

	public QuizRatingQuestionsDTO makeQuizRatingQuestionsFromQuiz(Quiz quiz) {
		UUID quizId = quiz.getId();
		Double rating = attemptRepository.findQuizRating(quizId);
		List<Question> questions = getAmountOfQuestionsInQuiz(quizId);

		QuizRatingQuestionsDTO quizRatingQuestionsDTO = new QuizRatingQuestionsDTO(quiz, rating, questions);
		return quizRatingQuestionsDTO;

	}

	public List<Question> getAmountOfQuestionsInQuiz(UUID quizId) {
		List<Question> questions = questionRepository.findQuestionsByQuizId(quizId);
		return questions;
	}

	@Transactional
	public Quiz updateQuiz(String quizId, QuizUpdateDTO quizDTO, User currentUser) {
	    // 1. Busca o quiz no banco de dados ou lança uma exceção se não for encontrado.
	    Quiz quizInDB = quizRepository.findById(UUID.fromString(quizId))
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz não encontrado"));

	    // 2. Verifica se o usuário atual tem permissão para editar este quiz.
	    if (!quizInDB.getUser().getUserId().equals(currentUser.getUserId())) {
	        throw new AccessDeniedException("Você não tem permissão para editar este quiz.");
	    }

	    // 3. Mapeia os dados do DTO (Data Transfer Object) para a entidade que está no banco.
	    quizMapper.toEntityUpdate(quizDTO, quizInDB);

	    // 4. ETAPA CRÍTICA: Estabelece a relação bidirecional entre as entidades.
	    // Isso garante que cada Resposta (Answer) saiba a qual Pergunta (Question) pertence.
	    if (quizDTO.questions() != null) {
	    	
	    	// Mapa para encontrar as questões existentes de forma rápida pelo ID
	        Map<UUID, Question> questionMap = quizInDB.getQuestions()
	        		.stream()
	        		.collect(Collectors.toMap(Question::getQuestionId, Function.identity()));
	     
	        // Lista para guardar as questões processadas (novas e atualizadas)
	        List<Question> processedQuestions = new ArrayList<>();
	        
	        for(UpdateQuestionDTO questionDTO : quizDTO.questions()) {
	        	Question question;
	        	if(questionDTO.questionId() != null && !questionDTO.questionId().isBlank()) {
	        		// É uma questão existente, atualizá-la
	        		question = questionMap.get(UUID.fromString(questionDTO.questionId()));
	        		
	        		if(question == null) {
	        			// Se o ID não for encontrado, tratamos como uma nova questão
	        			question = new Question();
	        			question.setQuiz(quizInDB);
	        		}
	        	}else {
	        		question = new Question();
        			question.setQuiz(quizInDB);
	        	}
	        	question.setText(questionDTO.text());
	        	
	        	if(questionDTO.answers() !=null) {
	        		Map<UUID, Answer> answerMap = question.getAnswers() != null ? question.getAnswers()
	        				.stream().collect(Collectors.toMap(Answer::getAnswerId, Function.identity())) :new HashMap<>();
	        		
	        		List<Answer> processedAnswer = new ArrayList<>();
	        		
	        		for (UpdateAnswersDTO answerDTO : questionDTO.answers()) {
	        			Answer answer;
	        			
	        			if(answerDTO.answerId() != null && !answerDTO.answerId().isBlank()) {
	        				answer = answerMap.get(UUID.fromString(answerDTO.answerId()));
	        				if(answer == null) {
	        					answer = new Answer();
	        					answer.setQuestion(question);
	        				}
	        			}else {
	        				answer = new Answer();
        					answer.setQuestion(question);
	        			}
	        			answer.setText(answerDTO.text());
	        			answer.setCorrect(answerDTO.correct());
	        			answer.setExplanation(answerDTO.explanation());
	        			processedAnswer.add(answer);
	        		}
	        		if(question.getAnswers() == null) question.setAnswers(new ArrayList<>());
	        		question.getAnswers().clear();
	        		question.getAnswers().addAll(processedAnswer);
	        	}
	        	processedQuestions.add(question);
	        }
	        quizInDB.getQuestions().clear();
		    quizInDB.getQuestions().addAll(processedQuestions);
	    }

	   

	    // 5. Atualiza as tags do quiz, se houver alguma no DTO.
	    if (quizDTO.tagsId() != null) {
	        quizInDB.getTags().clear(); // Limpa as tags antigas.
	        Set<Tags> foundTags = findTagsFindById(quizDTO.tagsId());
	        quizInDB.setTags(foundTags); // Adiciona as novas tags.
	    }

	    // 6. Salva o quiz atualizado no banco de dados.
	    return quizRepository.save(quizInDB);
	}
}
