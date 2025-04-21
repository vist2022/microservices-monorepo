package daily_farm.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

public class UserDetailsWithId extends User {

	private static final long serialVersionUID = -996623878022015319L;
	@Getter
	private final UUID id;

	public UserDetailsWithId(String username, String password, Collection<? extends GrantedAuthority> authorities,
			UUID id) {
		super(username, password, authorities);
		this.id = id;
	}
	
	
}
