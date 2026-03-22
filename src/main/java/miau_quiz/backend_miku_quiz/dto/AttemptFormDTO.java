package miau_quiz.backend_miku_quiz.dto;

import java.util.List;

import miau_quiz.backend_miku_quiz.forms.AttemptAnswer;



public record AttemptFormDTO(
		
		List<AttemptAnswer> attemptAnswers
		//Integer rating
		) {

}
