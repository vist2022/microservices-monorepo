package daily_farm.security.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


import lombok.Getter;

@RedisHash
@Getter
public class BlacklistedToken {

	@Id
	private String token;
	private Instant expiresAt;

	public BlacklistedToken() {
	}

	public BlacklistedToken(String token, Instant expiresAt) {
		this.token = token;
		this.expiresAt = expiresAt;
	}

	public String getToken() {
		return token;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
}
