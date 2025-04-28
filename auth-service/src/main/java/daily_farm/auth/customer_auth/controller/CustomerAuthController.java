package daily_farm.auth.customer_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.auth.customer_auth.service.CustomerAuthService;

import static daily_farm.auth.api.AuthApiConstants.*;

import daily_farm.auth.api.dto.ChangePasswordRequest;
import daily_farm.auth.api.dto.LoginRequestDto;
import daily_farm.auth.api.dto.SendToRequestDto;
import daily_farm.auth.api.dto.customer.*;
import daily_farm.auth.api.dto.tokens.RefreshTokenResponseDto;
import daily_farm.auth.api.dto.tokens.TokensResponseDto;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthController {

   
    private final CustomerAuthService authService;
    
  //Registration and verification

    @PostMapping(CUSTOMER_REGISTER)
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegistrationDto customerDto,
    		@RequestHeader(value = "Accept-Language", defaultValue = "en") String browserLanguage,
			@RequestHeader(value = "X-User-Language", required = false) String userLanguage) {
       
    	log.info("Registering new customer: {}", customerDto.getEmail());
    	
    	String language;
		if(userLanguage == null || userLanguage.isBlank())
			language = browserLanguage != null ? browserLanguage.split(",")[0] : "en";
		else
			language = userLanguage;
        return authService.registerCustomer(customerDto, language);
    }
    
    @GetMapping(CUSTOMER_EMAIL_VERIFICATION)
    public ResponseEntity<String> emailVerification(@RequestParam String token) {
        log.info("Received request to verify email. Token: {}", token);
   
        return authService.emailVerification(token);
    }
    
    @GetMapping(CUSTOMER_EMAIL_VERIFICATION_RESEND)
    public ResponseEntity<String> resendVerificationLink(@Valid @RequestBody SendToRequestDto sendToRequestDto) {
    	log.info("Controller. Resend verification link");
        return authService.resendVerificationLink(sendToRequestDto.getEmail());
    }
    
    //Authorization and logout

    @PostMapping(CUSTOMER_LOGIN)
    public ResponseEntity<TokensResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.loginCustomer(loginRequestDto);
    }
    
    @DeleteMapping(CUSTOMER_LOGOUT)
    public ResponseEntity<String> logoutCustomer(@RequestHeader(value = "X-User-Id", required = true) String id,
    		@RequestHeader("Authorization") String token) {
		
    		try {
                UUID userId = UUID.fromString(id);
                authService.logoutCustomer(userId, token);
                return ResponseEntity.ok("LOGOUT_SUCCESS");
            } catch (IllegalArgumentException e) {
                log.error("Invalid X-User-Id format: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
            }
    }
    
  //Changing and restoring the password
    
    @PutMapping(CUSTOMER_CHANGE_PASSWORD)
    public ResponseEntity<TokensResponseDto> customerUpdatePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordDto,
			@RequestHeader("X-User-Id") UUID id,
			 @RequestHeader("Authorization") String token) {
        return authService.updatePassword( id,  changePasswordDto);
    }
    
    @GetMapping(CUSTOMER_RESET_PASSWORD)
    public ResponseEntity<String> generateAndSendNewPassword(@Valid @RequestBody SendToRequestDto sendToRequestDto) {
        return authService.generateAndSendNewPassword(sendToRequestDto.getEmail());
    }
    

    
    //Deleting an account
    
    @DeleteMapping(CUSTOMER_REMOVE)
    public ResponseEntity<String> removeCustomer(@RequestHeader(value = "X-User-Id", required = true) String id,
			 @RequestHeader("Authorization") String token) {

		try {
           UUID userId = UUID.fromString(id);
           return authService.removeCustomer(userId);
       } catch (IllegalArgumentException e) {
           log.error("Invalid X-User-Id format: {}", id);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
       }
    }
    
  //Email Update


    @PutMapping(CUSTOMER_CHANGE_EMAIL)
    public ResponseEntity<String> customerUpdateEmail(@RequestParam String token) {
        return authService.updateEmail(token);
    }
    
 @GetMapping(CUSTOMER_EMAIL_CHANGE_VERIFICATION)
 public ResponseEntity<String> sendVerificationTokenForUpdateEmail(@RequestBody CustomerUpdateEmailRequestDto data) {
     return authService.sendVerificationTokenForUpdateEmail(data.getId(), data.getEmail());
 }

 @GetMapping(CUSTOMER_NEW_EMAIL_VERIFICATION)
 public ResponseEntity<String> sendVerificationTokenToNewEmail(@RequestParam String token) {
     return authService.sendVerificationTokenToNewEmail(token);
 }

// Other
	@PostMapping(CUSTOMER_REFRESH_TOKEN)
	public ResponseEntity<RefreshTokenResponseDto> refresh(
	        @RequestBody RefreshTokenRequestDto request) {
	    log.info("Controller: Refresh token starts");
	    return authService.refreshAccessTokenCustomer(request.getRefreshToken());
	}
}
