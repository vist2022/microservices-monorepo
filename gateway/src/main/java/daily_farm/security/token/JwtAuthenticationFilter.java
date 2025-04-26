package daily_farm.security.token;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import daily_farm.security.api.PulicEndpoints;


@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
	
	@Autowired
	private  JwtService jwtService;
	@Autowired
	private  TokenBlacklistService blackListService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.debug("JwtAuthenticationFilter invoked for path: {}", exchange.getRequest().getPath());
		String requestURI = exchange.getRequest().getURI().getPath();
		log.info("GatewayFilter. requestURI" + requestURI);
		if (PulicEndpoints.isPublicEndpoint(requestURI)) {
			log.info("OncePerRequestFilter. Request does not need token");
			return chain.filter(exchange);
		}

		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		log.info("GatewayFilter. Token received from header");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			log.info("GatewayFilter. Token is not null and starts with Bearer. ");

			try {
				
				if (!jwtService.isValidTokenSigned(token)) {
					log.error("GatewayFilter. Token is not valid");
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}
				
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

				String email = jwtService.extractUserEmail(token);
				log.info("GatewayFilter. email recived from token - {}", email);

				String role = jwtService.extractUserRole(token);
				log.info("GatewayFilter. Role recived from token - {}", role);

				String userId = jwtService.extractUserId(token);
				log.info("GatewayFilter. userId recived from token - {}", userId);

				if (email == null || role == null || userId == null) {
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}
				log.info("Adding headers: X-User-Id={}, X-User-Role={}, X-User-Email={}", userId, role, email);
				ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Id", userId).header("X-User-Role", role)
						.header("X-User-Email", email).header("Authorization", "Bearer " + token).build();
				log.debug("After adding headers: {}", mutatedRequest.getHeaders());
				
				ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
				return chain.filter(mutatedExchange);
				
				
			} catch (ExpiredJwtException | SecurityException | MalformedJwtException e) {
				  exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	                return exchange.getResponse().setComplete();
			}
		}
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return -1;
	}

}