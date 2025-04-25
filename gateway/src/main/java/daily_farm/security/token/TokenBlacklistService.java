package daily_farm.security.token;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class TokenBlacklistService {

	@Autowired 
	JwtService jwtService;
	@Autowired
	private StringRedisTemplate redisTemplate;

    public void addToBlacklist(String token) {
    	long expirationMillis = jwtService.extractExpiration(token).getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

}
