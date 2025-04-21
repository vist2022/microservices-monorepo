package daily_farm.security.farmer_auth.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import daily_farm.security.api.dto.ChangePasswordRequest;
import daily_farm.security.api.dto.FarmerRegistrationDto;
import daily_farm.security.api.dto.LoginRequestDto;
import daily_farm.security.api.dto.TokensResponseDto;

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
