package miau_quiz.backend_miku_quiz.dto;

import java.util.List;

public record CreateQuestionDTO(
		String text,
		List<CreateAnswersDTO> answers) {

}
