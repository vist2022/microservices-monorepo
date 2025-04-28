package daily_farm.security.token;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
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
    	log.debug("TokenBlacklistService. Resieved token {} for check", token );
    	
    	boolean res = redisTemplate.hasKey(token);
    	log.debug("TokenBlacklistService. Resieved token balcklisted - {}", res );
    	
        return res;
    }

}
