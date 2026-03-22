package miau_quiz.backend_miku_quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDTO(
		@JsonProperty("access_token") String accessToken,
		@JsonProperty("refresh_token") String refreshToken,
		@JsonProperty("expires_in") Long expiresIn
		
		
		) {

}
