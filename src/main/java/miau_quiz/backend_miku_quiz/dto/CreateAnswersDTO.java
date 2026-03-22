package miau_quiz.backend_miku_quiz.dto;

public record CreateAnswersDTO(
		String text,
		boolean correct,
		String explanation) {

}
