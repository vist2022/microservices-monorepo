package daily_farm.auth.farmer_auth.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import daily_farm.auth.api.dto.ChangePasswordRequest;
import daily_farm.auth.api.dto.FarmerRegistrationDto;
import daily_farm.auth.api.dto.LoginRequestDto;
import daily_farm.auth.api.dto.tokens.TokensResponseDto;

public interface IFarmerAuth {
	
	ResponseEntity<String> registerFarmer(FarmerRegistrationDto farmerDto, String lang);

	ResponseEntity<String> emailVerification(String verificationToken);

	ResponseEntity<String> resendVerificationLink(String email);

	ResponseEntity<String> removeFarmer(UUID id);

	ResponseEntity<TokensResponseDto> loginFarmer(LoginRequestDto loginRequestDto);

	ResponseEntity<String> logoutFarmer(UUID id, String token);

	ResponseEntity<TokensResponseDto> updatePassword(UUID uuid, ChangePasswordRequest changePasswordDto);
	
	ResponseEntity<String> generateAndSendNewPassword(String email);

	ResponseEntity<String> sendVerificationTokenForUpdateEmail(UUID id, String newEmail);

	ResponseEntity<String> sendVerificationTokenToNewEmail(String token);
	
	ResponseEntity<String> updateEmail(String token);

}
