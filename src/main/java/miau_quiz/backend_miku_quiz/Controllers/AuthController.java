package miau_quiz.backend_miku_quiz.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.AccountCredentialsDTO;
import miau_quiz.backend_miku_quiz.dto.ForgotPasswordDTO;
import miau_quiz.backend_miku_quiz.dto.SingupCredentialsDTO;
import miau_quiz.backend_miku_quiz.dto.TokenDTO;
import miau_quiz.backend_miku_quiz.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody @Valid SingupCredentialsDTO dto ){
		authService.register(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDTO> login(@RequestBody @Valid AccountCredentialsDTO dto){
		TokenDTO token = authService.login(dto);
		return ResponseEntity.ok(token);
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgtPassword(@RequestBody @Valid ForgotPasswordDTO dto){
		String message = authService.forgotPassword(dto.email());
		return ResponseEntity.ok(message);
	}
}
