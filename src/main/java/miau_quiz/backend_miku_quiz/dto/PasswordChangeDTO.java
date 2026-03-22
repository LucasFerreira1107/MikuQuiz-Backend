package miau_quiz.backend_miku_quiz.dto;

public record PasswordChangeDTO(
		String name,
		String oldPassword,
		String newPassword) {

}
