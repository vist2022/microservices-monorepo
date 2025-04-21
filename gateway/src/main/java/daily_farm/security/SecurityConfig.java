package daily_farm.security;

import lombok.RequiredArgsConstructor;
import daily_farm.security.customer_auth.CustomerDetailsService;
import daily_farm.security.farmer_auth.FarmerDetailsService;
import daily_farm.security.token.JwtService;
import daily_farm.security.token.TokenBlacklistService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtService jwtService;
	private final FarmerDetailsService farmerDetailsService;
	private final CustomerDetailsService customerDetailsService;
    private final TokenBlacklistService blackListService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PulicEndpoints.getPublicEndpoinstWithPrefix()).permitAll()

                        .requestMatchers("/farmer/**").hasRole("FARMER")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER") 
                        .anyRequest().authenticated()
                        
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint) )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, farmerDetailsService, customerDetailsService , blackListService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.setAllowedOrigins(List.of("*")); 
       configuration.setAllowedMethods(List.of("*"));
       configuration.setAllowedHeaders(List.of("*")); 
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
   }
    
  
}
