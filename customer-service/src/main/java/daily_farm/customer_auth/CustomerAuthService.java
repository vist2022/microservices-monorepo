package daily_farm.customer_auth;

import static daily_farm.api.messages.ErrorMessages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.entity.Customer;
import daily_farm.entity.CustomerCredential;
import daily_farm.repo.CustomerCredentialRepository;
import daily_farm.repo.CustomerRepository;
import daily_farm.api.dto.RefreshTokenResponseDto;
import daily_farm.api.dto.TokensResponseDto;
import daily_farm.token.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthService {
	private final CustomerRepository customerRepo;
	private final CustomerCredentialRepository customerCredentialRepo;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;


	public TokensResponseDto authenticateCustomer(String email, String password) {

		Optional<Customer> customerOptional = customerRepo.findByEmail(email);
		if (customerOptional.isPresent()) {
			Customer customer = customerOptional.get();
			CustomerCredential credential = customerCredentialRepo.findByCustomer(customer);
			log.info("Authenticate. Customer " + customer.getEmail() + " exists");
			log.info("Authenticate. passwordEncoder.matches"
					+ passwordEncoder.matches(password, credential.getHashedPassword()));
			if (passwordEncoder.matches(password, credential.getHashedPassword())) {
				log.info("Authenticate. Password is valid");
				String uuid = customer.getId().toString();
				String accessToken = jwtService.generateAccessToken(uuid, email, "customer");
				String refreshToken = jwtService.generateRefreshToken(uuid, email, "customer");
				credential.setRefreshToken(refreshToken);
				customerCredentialRepo.save(credential);
				return new TokensResponseDto(accessToken, refreshToken);
			}
		}
		throw new BadCredentialsException(WRONG_USER_NAME_OR_PASSWORD);
	}

	
	public ResponseEntity<RefreshTokenResponseDto> refreshAccessTokenCustomer(String refreshToken) {

		log.info("AuthService refreshAccessToken. Refresh access token starts - " + refreshToken);

		UUID id = UUID.fromString(jwtService.extractUserId(refreshToken));
		Optional<Customer> customerOptional = customerRepo.findById(id);
		CustomerCredential customerCredential = customerCredentialRepo.findByCustomer(new Customer(id));
		if (customerOptional.isPresent() && !customerCredential.getRefreshToken().isBlank()
				&& customerCredential.getRefreshToken().equals(refreshToken)
				&& !jwtService.isTokenExpired(refreshToken)) {
			return ResponseEntity.ok(new RefreshTokenResponseDto(
					jwtService.generateAccessToken(id.toString(), customerOptional.get().getEmail(),jwtService.extractUserRole(refreshToken))));
		}
		throw new BadCredentialsException(INVALID_TOKEN);
	}
	
	
}
