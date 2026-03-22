package miau_quiz.backend_miku_quiz.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.service.UserService;


@RequiredArgsConstructor
public class JwtCustomAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {

		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt,
					userDetailsService.loadUserByUsername(jwtTokenProvider.getUsernameFromToken(jwt)))) {
				String username = jwtTokenProvider.getUsernameFromToken(jwt);
				User user = userService.getUserByEmail(username);

				if (user != null) {
					CustomAuthentication authentication = new CustomAuthentication(user);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(request, response);
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
