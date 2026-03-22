package miau_quiz.backend_miku_quiz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.AttemptFormDTO;
import miau_quiz.backend_miku_quiz.dto.AttemptResultDTO;
import miau_quiz.backend_miku_quiz.dto.QuestionResultDTO;
import miau_quiz.backend_miku_quiz.entity.Answer;
import miau_quiz.backend_miku_quiz.entity.Attempt;
import miau_quiz.backend_miku_quiz.entity.Question;
import miau_quiz.backend_miku_quiz.entity.Quiz;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.forms.AttemptAnswer;
import miau_quiz.backend_miku_quiz.mappers.AnswerFeedbackMapper;
import miau_quiz.backend_miku_quiz.repository.AttemptRepository;
import miau_quiz.backend_miku_quiz.repository.QuizRepository;
import miau_quiz.backend_miku_quiz.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AttemptService {

	private final AttemptRepository attemptRepository;
	private final UserRepository userRepository;
	private final CommonService commonService;
	private final AnswerFeedbackMapper answerFeedbackMapper;
	private final QuizRepository quizRepository;
	private final UserService userService;

	// Method to save attempt to database and return the score to the client-side:
	public AttemptResultDTO sendAttempt(AttemptFormDTO attemptFormDTO, String quizId, User currentUser) {
		Quiz quiz = commonService.checkQuizById(quizId);

		List<Question> questionsQuiz = quiz.getQuestions();
		List<AttemptAnswer> attemptAnswers = checkAnswersSizeEqualsQuestionsSize(attemptFormDTO, quiz, questionsQuiz);

		Map<UUID, Answer> correctAnswersMap = questionsQuiz.stream().flatMap(q -> q.getAnswers().stream())
				.filter(Answer::isCorrect)
				.collect(Collectors.toMap(a -> a.getQuestion().getQuestionId(), Function.identity()));

		AttemptResultDTO attemptResultDTO = findStatisticAndSave(attemptAnswers, quiz, correctAnswersMap, currentUser,
				attemptFormDTO);

		return attemptResultDTO;
	}


	private List<AttemptAnswer> checkAnswersSizeEqualsQuestionsSize(AttemptFormDTO attemptFormDTO, Quiz quiz,
			List<Question> questionsQuiz) {
		List<AttemptAnswer> attemptAnswers = attemptFormDTO.attemptAnswers();

		if (questionsQuiz.size() != attemptAnswers.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O número de respostas não corresponde ao número de questões do quiz.");
		}

		return attemptAnswers;
	}

	private AttemptResultDTO findStatisticAndSave(List<AttemptAnswer> attemptAnswers, Quiz quiz, Map<UUID, Answer> correctAnswersMap, User currentUser, AttemptFormDTO dto) {
		Integer score = 0;
		Integer currentStreak = 0;
		Integer maxStreak = 0;
		List<QuestionResultDTO> detailedResults = new ArrayList<>();
		
		
		for (AttemptAnswer userAnswer : attemptAnswers) {
			UUID questionId = UUID.fromString(userAnswer.getQuestionId());
			UUID answerId = UUID.fromString(userAnswer.getAnswerId());
			Answer correctAnswers = correctAnswersMap.get(questionId);
			Question currentQuestion = findQuestionById(quiz.getQuestions(),questionId);

			boolean isCorrect = correctAnswers != null && correctAnswers.getAnswerId().equals(answerId);
			
			if (isCorrect) {
				score += 100;
				currentStreak++;
			} else {
				currentStreak = 0;
			}

			if (currentStreak > maxStreak) {
				maxStreak = currentStreak;
			}
			detailedResults.add(new QuestionResultDTO(
					questionId,
					currentQuestion.getText(),
					answerId,
					answerFeedbackMapper.toDTO(correctAnswers),
					isCorrect));			
		}
		
		int totalQuestions = quiz.getQuestions().size();
		double accuracy = (totalQuestions > 0) ? (double) (score/100) / totalQuestions : 0.0;		
		
		
		Attempt attempt = new Attempt();
		attempt.setScore(score);
		attempt.setQuiz(quiz);
		attempt.setUser(currentUser);
		attempt.setAccuracy(accuracy);
		attempt.setMaxStreak(maxStreak);
		attempt.setRating(5);
		attemptRepository.save(attempt);
		
		// Atualiza experiência do usuário
		int maxPossibleScore = totalQuestions * 100; // Pontuação máxima possível
		boolean levelUp = userService.updateUserExperience(
			currentUser.getUserId(), 
			score, 
			maxPossibleScore, 
			accuracy, 
			maxStreak
		);
		
		quiz.setPlaysCount(quiz.getPlaysCount()+1);
		quizRepository.save(quiz);
		
		AttemptResultDTO attemptResultDTO = new AttemptResultDTO(score, quiz.getQuestions().size(), accuracy, maxStreak, detailedResults, levelUp);
		return attemptResultDTO;
		
	}

	private void validateAttemptAnswer(Question question, Answer answer, UUID quizId) {

		if (!answer.getQuestion().equals(question)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more answers don't match corresponding question");
		}

		if (question.getQuiz().getId() != quizId) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Some of questions are not in the corresponding quiz");
		}

	}

	private Question findQuestionById(List<Question> questions, UUID questionID) {
		return questions.stream().filter(q -> q.getQuestionId().equals(questionID)).findFirst().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de questão inválido na submissão."));
	}
}
