package miau_quiz.backend_miku_quiz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.entity.Answer;
import miau_quiz.backend_miku_quiz.entity.Question;
import miau_quiz.backend_miku_quiz.entity.Quiz;
import miau_quiz.backend_miku_quiz.repository.AnswerRepository;
import miau_quiz.backend_miku_quiz.repository.QuestionRepository;
import miau_quiz.backend_miku_quiz.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final UserRepository userRepository;
	private final AnswerRepository answerRepository;
	private final CommonService commonService;

	// Method to get list of questions for the quiz: if the user is authenticated
	// and is the quiz's owner they can see correct answers, otherwise they cannot
	public List<Question> getQuestionsByQuizId(String quizId) {
		Quiz quiz = commonService.checkQuizById(quizId);

		List<Question> questions = questionRepository.findQuestionsByQuizId(UUID.fromString(quizId));

		return hideCorrectAnswers(questions);
	}

	// Method to save questions and answers for these questions:
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<?> saveQuestions(String quizId, List<Question> questions) {
		Quiz quizInDB = commonService.checkQuizById(quizId);
		UUID userId = quizInDB.getUser().getUserId();

		commonService.checkQuizStatus(quizInDB);
		saveQuestionsInDB(questions);

		return new ResponseEntity("Tudo salvo com sucesso", HttpStatus.CREATED);
	}

	@Transactional
	private void saveQuestionsInDB(List<Question> questions) {
		Question questionTemp;

		for (Question question : questions) {
			questionTemp = questionRepository.findById(question.getQuestionId()).orElse(null);
			if (questionTemp == null) {
				createNewQuestionWithAnswers(question);
			} else {
				updateExistingQuestionAndAnswers(question, question.getQuestionId());
			}
		}

	}

	private ResponseEntity<?> deleteQuestionById(UUID questionId) {
		Question question = commonService.findQuestionByID(questionId);
		Quiz quiz = question.getQuiz();

		UUID onwerId = quiz.getUser().getUserId();
		commonService.checkQuizStatus(quiz);
		questionRepository.delete(question);
		return new ResponseEntity<>("Pergunta deletada com sucesso", HttpStatus.NO_CONTENT);
	}

	private List<Question> hideCorrectAnswers(List<Question> questions) {
		questions.forEach(question -> question.getAnswers().forEach(answer -> answer.setCorrect(false)));
		return questions;
	}

	private void createNewQuestionWithAnswers(Question question) {
		Question newQuestion = new Question();
		newQuestion.setText(question.getText());
		newQuestion.setQuiz(question.getQuiz());

		questionRepository.save(newQuestion);

		List<Answer> answers = question.getAnswers();
		for (Answer answer : answers) {
			createNewAnswer(answer, newQuestion);
		}
	}

	private void createNewAnswer(Answer answer, Question newQuestion) {
		Answer newAnswer = new Answer();
		newAnswer.setText(answer.getText());
		newAnswer.setQuestion(newQuestion);
		newAnswer.setCorrect(answer.isCorrect());

		answerRepository.save(newAnswer);
	}

	private void updateExistingQuestionAndAnswers(Question updatedQuestion, UUID questionId) {
		Question newQuestion = commonService.findQuestionByID(questionId);
		newQuestion.setText(updatedQuestion.getText());
		questionRepository.save(newQuestion);

		List<Answer> answers = updatedQuestion.getAnswers();

		for (Answer answer : answers) {
			updateExistingAnswer(answer);
		}

	}

	private void updateExistingAnswer(Answer answer) {

		var answerId = answer.getAnswerId();
		Answer newAnswer = commonService.findAnswerByID((answerId));

		newAnswer.setText(answer.getText());
		newAnswer.setCorrect(answer.isCorrect());
		answerRepository.save(newAnswer);

	}
}
