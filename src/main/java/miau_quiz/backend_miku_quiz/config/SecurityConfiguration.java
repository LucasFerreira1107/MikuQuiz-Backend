package miau_quiz.backend_miku_quiz.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import miau_quiz.backend_miku_quiz.repository.UserRepository;
import miau_quiz.backend_miku_quiz.security.JwtCustomAuthenticationFilter;
import miau_quiz.backend_miku_quiz.security.JwtTokenProvider;
import miau_quiz.backend_miku_quiz.security.LoginSocialSuccessHandler;
import miau_quiz.backend_miku_quiz.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
	
	@Bean
	public JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter(
		JwtTokenProvider jwtTokenProvider,
		UserDetailsService userDetailsService,
		UserService userService
	) {
		return new JwtCustomAuthenticationFilter(jwtTokenProvider, userDetailsService, userService);
	}

	@Bean
	public SecurityFilterChain sourceSecurityFilterChain(HttpSecurity http, LoginSocialSuccessHandler successHandler,JwtCustomAuthenticationFilter jwtTokenFilter )
			throws Exception {

		return http.csrf(AbstractHttpConfigurer::disable)
			
			.authorizeHttpRequests(authorize -> {
			authorize.requestMatchers(
					"/error", 
					"/api/tags",
					"/api/quizzes",
					"/api/auth/**",
					"/login/**",
					"/oauth2/**").permitAll();
			authorize.anyRequest().authenticated();
		})
			.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
			.oauth2Login(oauth2 -> {
			oauth2.successHandler(successHandler);
		}).build();
	}

	// Configura o prefixo role
	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	@Bean
	public UserDetailsService userDetailsService(UserRepository repository) {
		return username ->{
			System.out.println("--- UserDetailsService: Procurando por utilizador ---");
	        System.out.println("Recebido username (email): " + username);
	        
			miau_quiz.backend_miku_quiz.entity.User userEntity = repository.findByEmail(username);
			if (userEntity == null) {
                throw new UsernameNotFoundException("Utilizador não encontrado: " + username);
            }
			System.out.println("Utilizador encontrado: " + userEntity.getName());
	        System.out.println("Senha encriptada do DB: " + userEntity.getPassword());
	        
	        if (userEntity.getRoles() == null || userEntity.getRoles().isEmpty()) {
	            System.err.println("!!! AVISO: Utilizador '" + username + "' não possui roles definidas. !!!");
	        } else {
	            System.out.println("Roles do utilizador: " + userEntity.getRoles());
	        }
			
			 List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
	                    .map(SimpleGrantedAuthority::new)
	                    .toList();
			 return  new User(userEntity.getEmail(), userEntity.getPassword(), authorities);
		};
	}
}
