package miau_quiz.backend_miku_quiz.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.Enums.AuthProvider;
import miau_quiz.backend_miku_quiz.dto.AccountCredentialsDTO;
import miau_quiz.backend_miku_quiz.dto.SingupCredentialsDTO;
import miau_quiz.backend_miku_quiz.dto.TokenDTO;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final AuthenticationManager authenticationManager; // Para validar as credenciais
	private final JwtTokenProvider jwtTokenProvider;    
	

	public void register(SingupCredentialsDTO dto) {
		if (userService.getUserByEmail(dto.email()) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este email ja esta sendo utilizado");
		}
		User user = new User();

		user.setName(dto.name());
		user.setLogin(dto.email());
		user.setEmail(dto.email());
		// A senha será criptografada automaticamente no UserService.save()
		user.setPassword(dto.password());
		user.setRoles(List.of("USER"));
		user.setProvider(AuthProvider.LOCAL);

		userService.save(user);
	}

	public TokenDTO login(AccountCredentialsDTO dto) {
		// 1. Cria um objeto de autenticação com as credenciais fornecidas
		var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());

		// 2. Delega a validação para o AuthenticationManager do Spring
		// Ele usará o seu UserDetailsService e PasswordEncoder automaticamente
		Authentication authentication = authenticationManager.authenticate(usernamePassword);
		
		// 3. Se a autenticação for bem-sucedida, gera o token JWT
		String token = jwtTokenProvider.generateToken(authentication);
		
		// 4. Retorna o token para o cliente
		// O refresh token pode ser implementado mais tarde, por isso está nulo por agora.
		return new TokenDTO(token, null, 3600L);
	}

	public String forgotPassword(String email) {
		User user = userService.getUserByEmail(email);
		if (user == null) {
            return "Se um utilizador com este email existir, um email de recuperação foi enviado.";
        }
		
		String token = UUID.randomUUID().toString();
		user.setResetPasswordToken(token);
		user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
		userService.save(user);
		System.out.println("TOKEN DE RESET (para teste): " + token);
		return "Se um utilizador com este email existir, um email de recuperação foi enviado.";

	}
}
