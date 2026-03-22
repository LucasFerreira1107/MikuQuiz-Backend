package miau_quiz.backend_miku_quiz.dto;

import java.util.UUID;

public record QuestionResultDTO(
		UUID questionId,
		String questionText,
		UUID userAnswerId,
		AnswerFeedbackDTO correctAnswer,
		boolean wasCorrect) {

}
