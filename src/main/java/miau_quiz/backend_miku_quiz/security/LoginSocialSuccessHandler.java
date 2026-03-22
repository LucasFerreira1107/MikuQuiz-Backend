package miau_quiz.backend_miku_quiz.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.Enums.AuthProvider;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.service.UserService;
import miau_quiz.backend_miku_quiz.security.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();

        String providerId = auth2AuthenticationToken.getAuthorizedClientRegistrationId();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userService.getUserByEmail(email);

        if(user == null){
            user = insertUserInDB(email, name, providerId);
        }

        // Cria uma nova autenticação com o usuário
        CustomAuthentication customAuth = new CustomAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(customAuth);

        // Gera o token JWT
        String token = jwtTokenProvider.generateToken(customAuth);
        
        // Redireciona para uma página de sucesso com o token
        String redirectUrl = String.format("/oauth2/success?token=%s", token);
        response.sendRedirect(redirectUrl);
    }
    
    private User insertUserInDB(String email, String name, String providerId) {
    	User user = new User();
    	user.setEmail(email);
    	user.setName(name != null ? name : getLoginByEmail(email));
    	user.setLogin(getLoginByEmail(email));

    	String passwordRandom = UUID.randomUUID().toString();
    	user.setPassword(passwordRandom);
    	user.setRoles(List.of("USER"));
    	
    	AuthProvider provider = AuthProvider.valueOf(providerId.toUpperCase());
    	user.setProvider(provider);

        userService.save(user);
        
        return user;
    }
    
    private String getLoginByEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }
}
