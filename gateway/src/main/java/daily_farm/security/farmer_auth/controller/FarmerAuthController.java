package daily_farm.security.farmer_auth.controller;

import static daily_farm.security.api.messages.ErrorMessages.LOCATION_REQUIRED;

import static daily_farm.security.api.AuthApiConstants.*;

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
import daily_farm.security.api.dto.SendToRequestDto;
import daily_farm.security.api.dto.CoordinatesDto;
import daily_farm.security.UserDetailsWithId;
import daily_farm.security.api.dto.*;
import daily_farm.security.farmer_auth.service.FarmerAuthService;

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
		log.info("Controller. Email verification starts - " + token);
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
	@PreAuthorize("hasRole('ROLE_FARMER')")
	public ResponseEntity<String> logoutFarmer(	@AuthenticationPrincipal UserDetailsWithId user,
			@Parameter(description = "JWT токен", required = true) @RequestHeader("Authorization") String token) {
		farmerAuth.logoutFarmer(user.getId(), token);
		return ResponseEntity.ok(LOGOUT_SUCCESS);
	}

	
	@PostMapping(FARMER_LOGIN)
	public ResponseEntity<TokensResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
		return farmerAuth.loginFarmer(loginRequestDto);
	}
	
	@GetMapping(FARMER_REFRESH_TOKEN)
	public ResponseEntity<RefreshTokenResponseDto> refresh(
			@Parameter(description = "JWT токен", required = true) @RequestBody RefreshTokenRequestDto request) {
		log.info("Controller/ refresh token starts");
		return farmerAuth.refreshAccessTokenFarmer(request.getRefreshToken());
	}

	@PutMapping(FARMER_CHANGE_PASSWORD)
	@PreAuthorize("hasRole(ROLE_FARMER)")
	public ResponseEntity<TokensResponseDto> farmerUpdatePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordDto,
			@AuthenticationPrincipal UserDetailsWithId user,
			@Parameter(description = "JWT токен", required = true) @RequestHeader("Authorization") String token) {
		return farmerAuth.updatePassword(user.getId(), changePasswordDto);
	}
	
	@DeleteMapping(FARMER_REMOVE)
	@PreAuthorize("hasRole('ROLE_FARMER')")
	public ResponseEntity<String> removeFarmer(@AuthenticationPrincipal UserDetailsWithId user,
			@Parameter(description = "JWT токен", required = true) @RequestHeader("Authorization") String token) {
		return farmerAuth.removeFarmer(user.getId());
	}
	
	@PreAuthorize("hasRole('ROLE_FARMER')")
	@GetMapping(FARMER_EMAIL_CHANGE_VERIFICATION)
	public ResponseEntity<String> sendVerificationTokenForUpdateEmail(@Valid @RequestBody SendToRequestDto sendToRequestDto,
			@AuthenticationPrincipal UserDetailsWithId user,
			@Parameter(description = "JWT токен", required = true) @RequestHeader("Authorization") String token){
		log.info("Controller. getEmailUpdateToken starts - " + sendToRequestDto.getEmail());
		return farmerAuth.sendVerificationTokenForUpdateEmail(user.getId(), sendToRequestDto.getEmail());
		
	}
	
	@GetMapping(FARMER_NEW_EMAIL_VERIFICATION)
	public ResponseEntity<String> getVerificationTokenToNewEmail(@RequestParam  String token){
		log.info("Controller. getVerificationTokenToNewEmail starts - " + token);
		return farmerAuth.sendVerificationTokenToNewEmail(token);
		
	}

	@PreAuthorize("hasRole('ROLE_FARMER')")
	@GetMapping(FARMER_CHANGE_EMAIL)
	public ResponseEntity<String> farmerUpdateEmail(@RequestParam String token,
			@AuthenticationPrincipal UserDetailsWithId user) {
		return farmerAuth.updateEmail(token);
	}
	

	
}
