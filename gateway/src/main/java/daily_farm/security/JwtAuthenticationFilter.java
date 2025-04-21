package daily_farm.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.security.customer_auth.CustomerDetailsService;
import daily_farm.security.farmer_auth.FarmerDetailsService;
import daily_farm.security.token.JwtService;
import daily_farm.security.token.TokenBlacklistService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import static daily_farm.security.api.messages.ErrorMessages.*;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final FarmerDetailsService farmerDetailsService;
	private final CustomerDetailsService customerDetailsService;
	private final TokenBlacklistService blackListService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("OncePerRequestFilter. requestURI" + requestURI);
		if (PulicEndpoints.isPublicEndpoint(requestURI)) {
			log.info("OncePerRequestFilter. Request does not need token");
			chain.doFilter(request, response);
			return;
		}

		String token = request.getHeader("Authorization");
		log.info("JwtAuthenticationFilter(OncePerRequestFilter). Token received from header " + token);
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			log.info("OncePerRequestFilter. Token is not null and starts with Bearer. ");

			try {
				if (blackListService.isBlacklisted(token)) {
					log.info("JwtAuthenticationFilter(OncePerRequestFilter). Token is blacklisted ");
					throw new JwtException(INVALID_TOKEN);
				}
				log.info("OncePerRequestFilter. token is expires - " + jwtService.isTokenExpired(token));
				String username = jwtService.extractUserEmail(token);
				log.info("JwtAuthenticationFilter(OncePerRequestFilter). User name recived from token - " + username);

				String role = jwtService.extractUserRole(token);
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = role.equals("farmer") ? farmerDetailsService.loadUserByUsername(username)
							: customerDetailsService.loadUserByUsername(username);

					log.info("OncePerRequestFilter. Recived userDetails + role");
					log.info("OncePerRequestFilter. jwtService.isTokenValid - {} ");

					if (jwtService.isTokenValid(token, userDetails.getUsername())
							&& jwtService.extractExpiration(token).after(new Date())) {
						log.info("OncePerRequestFilter. Token checked - valid");

						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				} else
					throw new JwtException(INVALID_TOKEN);
			} catch (ExpiredJwtException | SecurityException | MalformedJwtException e) {
				log.error("error" + e.getLocalizedMessage());
				request.setAttribute("JWT_ERROR", e.getMessage());
				throw new JwtException(e.getMessage());

			}
		}
		chain.doFilter(request, response);
	}
}