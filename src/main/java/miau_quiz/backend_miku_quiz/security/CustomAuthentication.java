package miau_quiz.backend_miku_quiz.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.entity.User;

@RequiredArgsConstructor
@Getter
public class CustomAuthentication implements Authentication {

	private static final long serialVersionUID = 1L;
	private final User user;
	
	@Override
	public String getName() {
		return user.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}
