package miau_quiz.backend_miku_quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountCredentialsDTO(
		@NotBlank(message = "Campo obrigatorio")
		@Size(min=2, max=100, message = "Campo fora do tamanho valido")
		String username,
		@NotBlank(message = "Campo obrigatorio")
		String password) {

}
