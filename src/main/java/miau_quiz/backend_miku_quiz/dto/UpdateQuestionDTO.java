package miau_quiz.backend_miku_quiz.dto;

import java.util.List;

public record UpdateQuestionDTO(
		String questionId,
		String text,
		List<UpdateAnswersDTO> answers) {

}
