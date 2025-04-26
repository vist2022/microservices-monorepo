package daily_farm.auth.farmer_auth.controller;

import static daily_farm.auth.api.messages.ErrorMessages.LOCATION_REQUIRED;

import java.util.UUID;

import static daily_farm.auth.api.AuthApiConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import daily_farm.farmer.api.dto.CoordinatesDto;
import daily_farm.auth.UserDetailsWithId;
import daily_farm.auth.api.dto.*;
import daily_farm.auth.api.dto.tokens.RefreshTokenRequestDto;
import daily_farm.auth.api.dto.tokens.RefreshTokenResponseDto;
import daily_farm.auth.api.dto.tokens.TokensResponseDto;
import daily_farm.auth.farmer_auth.service.FarmerAuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FarmerAuthController {

	private final FarmerAuthService farmerAuth;
	
	@PostMapping(FARMER_REGISTER)
	public ResponseEntity<String> registerFarmer(@Valid @RequestBody FarmerRegistrationDto farmerDto,
			@RequestHeader(value = "X-Latitude", required = false) Double latitude,
			@RequestHeader(value = "X-Longitude", required = false) Double longitude,
			@RequestHeader(value = "Accept-Language", defaultValue = "en") String browserLanguage,
			@RequestHeader(value = "X-User-Language", required = false) String userLanguage) {

		log.info("Controller. Request for registration new farmer - " + farmerDto.getEmail());
		if (latitude == null && longitude == null && farmerDto.getCoordinates() == null)
			return ResponseEntity.badRequest().body(LOCATION_REQUIRED);

		if (latitude != null && longitude != null && farmerDto.getCoordinates() == null) {
			farmerDto.setCoordinates(new CoordinatesDto(latitude, longitude));
			log.debug("Controller. There is not coordinates in body. Update coordinates in dto from header");
		}
		
		
		String language;
		if(userLanguage == null || userLanguage.isBlank())
			language = browserLanguage != null ? browserLanguage.split(",")[0] : "en";
		else
			language = userLanguage;
			
		return farmerAuth.registerFarmer(farmerDto, language);
	}
	
	@GetMapping(FARMER_EMAIL_VERIFICATION)
	public ResponseEntity<String> emailVerification(@RequestParam String token){
		log.info("Controller. Email verification starts - {} ", token);
		return farmerAuth.emailVerification(token);
		
	}
	
	@GetMapping(FARMER_EMAIL_VERIFICATION_RESEND)
	public ResponseEntity<String> resendVerificationLink(@Valid @RequestBody SendToRequestDto sendToRequestDto){
		log.info("Controller. Resend verification link");
		return farmerAuth.resendVerificationLink(sendToRequestDto.getEmail());
		
	}
	
	@GetMapping(RESET_PASSWORD)
	public ResponseEntity<String> generateAndSendNewPassword(@Valid @RequestBody SendToRequestDto sendToRequestDto){
		log.info("Controller. generateAndSendNewPassword starts - " + sendToRequestDto.getEmail());
		return farmerAuth.generateAndSendNewPassword(sendToRequestDto.getEmail());
		
	}
	
	@DeleteMapping(FARMER_LOGOUT)
	public ResponseEntity<String> logoutFarmer(@RequestHeader(value = "X-User-Id", required = true) String id,
		@RequestHeader("Authorization") String token) {
		
		try {
            UUID userId = UUID.fromString(id);
            farmerAuth.logoutFarmer(userId, token);
            return ResponseEntity.ok("LOGOUT_SUCCESS");
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
	
	}

	
	@PostMapping(FARMER_LOGIN)
	public ResponseEntity<TokensResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
		return farmerAuth.loginFarmer(loginRequestDto);
	}
	
	@GetMapping(FARMER_REFRESH_TOKEN)
	public ResponseEntity<RefreshTokenResponseDto> refresh(
			 @RequestBody RefreshTokenRequestDto request) {
		log.info("Controller/ refresh token starts");
		return farmerAuth.refreshAccessTokenFarmer(request.getRefreshToken());
	}

	@PutMapping(FARMER_CHANGE_PASSWORD)
	public ResponseEntity<TokensResponseDto> farmerUpdatePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordDto,
			@RequestHeader("X-User-Id") UUID id,
			 @RequestHeader("Authorization") String token) {
		return farmerAuth.updatePassword(id, changePasswordDto);
	}
	
	@DeleteMapping(FARMER_REMOVE)
	public ResponseEntity<String> removeFarmer(@RequestHeader(value = "X-User-Id", required = true) String id,
			 @RequestHeader("Authorization") String token) {

		try {
            UUID userId = UUID.fromString(id);
            return farmerAuth.removeFarmer(userId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
	}
	
	@GetMapping(FARMER_EMAIL_CHANGE_VERIFICATION)
	public ResponseEntity<String> sendVerificationTokenForUpdateEmail(@Valid @RequestBody SendToRequestDto sendToRequestDto,
			@RequestHeader("X-User-Id") UUID id,
			@RequestHeader("Authorization") String token){
		log.info("Controller. getEmailUpdateToken starts - " + sendToRequestDto.getEmail());
		return farmerAuth.sendVerificationTokenForUpdateEmail(id, sendToRequestDto.getEmail());
		
	}
	
	@GetMapping(FARMER_NEW_EMAIL_VERIFICATION)
	public ResponseEntity<String> getVerificationTokenToNewEmail(@RequestParam  String token){
		log.info("Controller. getVerificationTokenToNewEmail starts - " + token);
		return farmerAuth.sendVerificationTokenToNewEmail(token);
		
	}

	@GetMapping(FARMER_CHANGE_EMAIL)
	public ResponseEntity<String> farmerUpdateEmail(@RequestParam String token) {
		return farmerAuth.updateEmail(token);
	}
	

	
}
