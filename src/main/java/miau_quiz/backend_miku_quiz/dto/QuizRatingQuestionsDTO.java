package miau_quiz.backend_miku_quiz.dto;

import java.util.List;

import miau_quiz.backend_miku_quiz.entity.Question;
import miau_quiz.backend_miku_quiz.entity.Quiz;

public record QuizRatingQuestionsDTO(
		 Quiz quiz,
		 Double rating,
		 List<Question> questions) {

}
