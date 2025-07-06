package daily_farm.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import daily_farm.order.api.dto.FarmSetResponseForOrderDto;

@Configuration
public class RedisConfig {
	
	@Bean
	 RedisTemplate<String, FarmSetResponseForOrderDto> redisTemplate(RedisConnectionFactory connectionFactory) {
	    RedisTemplate<String, FarmSetResponseForOrderDto> template = new RedisTemplate<>();
	    
	    template.setConnectionFactory(connectionFactory);
	    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
	    template.setValueSerializer(serializer);
	    template.setKeySerializer(new StringRedisSerializer());
	    return template;
	}
	@Bean
	RedisTemplate<String, Integer> stockRedisTemplate(RedisConnectionFactory connectionFactory) {
	    RedisTemplate<String, Integer> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);
	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
	    return template;
	}
}
