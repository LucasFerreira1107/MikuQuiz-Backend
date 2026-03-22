package miau_quiz.backend_miku_quiz.dto;

import java.util.UUID;

public record AnswerFeedbackDTO(
		UUID answerId,
		String text,
		String explanation) {

	
}
