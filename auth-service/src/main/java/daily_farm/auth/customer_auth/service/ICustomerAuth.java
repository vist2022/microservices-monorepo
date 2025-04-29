package daily_farm.auth.customer_auth.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import daily_farm.auth.api.dto.ChangePasswordRequest;
import daily_farm.auth.api.dto.LoginRequestDto;
import daily_farm.auth.api.dto.customer.CustomerRegistrationDto;
import daily_farm.auth.api.dto.tokens.RefreshTokenResponseDto;
import daily_farm.auth.api.dto.tokens.TokensResponseDto;

public interface ICustomerAuth {
	
	ResponseEntity<String> registerCustomer(CustomerRegistrationDto customerDto , String lang);

	ResponseEntity<String> emailVerification(String verificationToken);

	ResponseEntity<String> resendVerificationLink(String email);

	ResponseEntity<String> removeCustomer(UUID id);

	ResponseEntity<TokensResponseDto> loginCustomer(LoginRequestDto loginRequestDto);

	ResponseEntity<String> logoutCustomer(UUID id, String token);

	ResponseEntity<TokensResponseDto> updatePassword(UUID uuid, ChangePasswordRequest changePasswordDto);
	
	ResponseEntity<String> generateAndSendNewPassword(String email);

	ResponseEntity<String> sendVerificationTokenForUpdateEmail(UUID id, String newEmail);

	ResponseEntity<String> sendVerificationTokenToNewEmail(String token);
	
	ResponseEntity<RefreshTokenResponseDto> refreshAccessTokenCustomer(String token);
	
	ResponseEntity<String> updateEmail(String token);

}
