package telran.daily_farm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	  @Bean
//	     RestTemplate restTemplate() {
//	        return new RestTemplate();
//	    }
	public RestTemplate restTemplate() {
	    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	    return new RestTemplate(factory);
	}
}
