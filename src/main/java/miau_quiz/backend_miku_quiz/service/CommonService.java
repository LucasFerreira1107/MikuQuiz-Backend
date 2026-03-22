package miau_quiz.backend_miku_quiz.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.Enums.QuizStatus;
import miau_quiz.backend_miku_quiz.entity.Answer;
import miau_quiz.backend_miku_quiz.entity.Question;
import miau_quiz.backend_miku_quiz.entity.Quiz;
import miau_quiz.backend_miku_quiz.repository.AnswerRepository;
import miau_quiz.backend_miku_quiz.repository.QuestionRepository;
import miau_quiz.backend_miku_quiz.repository.QuizRepository;

@Service
@RequiredArgsConstructor
public class CommonService {

//	private UserRepository userRepoistory;
	private final QuizRepository quizRepoistory;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepoistory;
	
	// Method to check user's authentication and rights to read information. If user
	// is authenticated and authorized then the user instance is returned
	
	
	// Method to check user's authentication
	
	
	
	// Method to check if there's a quiz in DB by provided ID:
	public Quiz checkQuizById(String id) {
		var quiz = quizRepoistory.findById(UUID.fromString(id)).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		return quiz;
	}
//	 Method to verify user;
//	public void verifyUser() {
//		
//	}
	
	// Method to check the status of quiz: user can't change published quizzes
	public void checkQuizStatus(Quiz quiz) {
		QuizStatus status = quiz.getStatus();
		
		if(status.equals(QuizStatus.PUBLISHED)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You can't update the quiz that is already published" );
		}
	}
	
	// Method to check if the question's in db by ID:
	public Question findQuestionByID(String id) {
		Optional<Question> optionalQuestion = questionRepository.findById(UUID.fromString(id));
		
		
		if(!optionalQuestion.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more of your questions that supposed to be in db can't be found in db");
		}
		
//		Question question = optionalQuestion.get();
//		return question;
		return optionalQuestion.get();
	}
	
	// Method to check if the answer is in DB by ID:
	public Answer findAnswerByID(String id) {
		Optional<Answer> optionalAnswer = answerRepoistory.findById(UUID.fromString(id));
		if(!optionalAnswer.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more of your questions that supposed to be in db can't be found in db");
		}
		
		Answer answer = optionalAnswer.get();
		return answer;
	}

	public Answer findAnswerByID(UUID id) {
		Optional<Answer> optionalAnswer = answerRepoistory.findById(id);
		if(!optionalAnswer.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more of your questions that supposed to be in db can't be found in db");
		}
		
		Answer answer = optionalAnswer.get();
		return answer;
	}

	public Question findQuestionByID(UUID questionId) {
Optional<Question> optionalQuestion = questionRepository.findById(questionId);
		
		
		if(!optionalQuestion.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more of your questions that supposed to be in db can't be found in db");
		}
		
//		Question question = optionalQuestion.get();
//		return question;
		return optionalQuestion.get();
	}
}
