package miau_quiz.backend_miku_quiz.dto;

import miau_quiz.backend_miku_quiz.entity.Quiz;

public record QuizRatingDTO(
		 Quiz quiz,
		 Double rating) {

}
