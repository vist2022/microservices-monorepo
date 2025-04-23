package daily_farm.security.token;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import daily_farm.security.api.PulicEndpoints;



@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {
	private final JwtService jwtService;
	private final TokenBlacklistService blackListService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String requestURI = exchange.getRequest().getURI().getPath();
		log.info("GatewayFilter. requestURI" + requestURI);
		if (PulicEndpoints.isPublicEndpoint(requestURI)) {
			log.info("OncePerRequestFilter. Request does not need token");
			return chain.filter(exchange);
		}

		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		log.info("GatewayFilter. Token received from header " + token);
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			log.info("GatewayFilter. Token is not null and starts with Bearer. ");

			try {
				if (blackListService.isBlacklisted(token)) {
					log.error("GatewayFilter. Token is blacklisted ");
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}

				if (jwtService.isTokenExpired(token)) {
					log.error("GatewayFilter. Token is expired");
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}

				String username = jwtService.extractUserEmail(token);
				log.info("GatewayFilter. User name recived from token - {}", username);

				String role = jwtService.extractUserRole(token);
				log.info("GatewayFilter. Role recived from token - {}", role);

				String userId = jwtService.extractUserId(token);
				log.info("GatewayFilter. userId recived from token - {}", userId);

				if (username == null || role == null || userId == null) {
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}

				exchange.getRequest().mutate().header("X-User-Id", userId).header("X-User-Role", role)
						.header("X-User-Email", username).build();

				
			} catch (ExpiredJwtException | SecurityException | MalformedJwtException e) {
				  exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	                return exchange.getResponse().setComplete();
			}
		}
		return chain.filter(exchange);
	}

}