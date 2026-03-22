package miau_quiz.backend_miku_quiz.dto;

public record UpdateAnswersDTO(
		String answerId,
		String text,
		boolean correct, 
		String explanation) {

}
