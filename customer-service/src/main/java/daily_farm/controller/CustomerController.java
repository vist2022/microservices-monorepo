package daily_farm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;

import daily_farm.service.ICustomer;
import daily_farm.api.dto.RefreshTokenRequestDto;
import daily_farm.api.dto.RefreshTokenResponseDto;
import daily_farm.api.dto.TokensResponseDto;
import daily_farm.customer_auth.CustomerAuthService;
import daily_farm.token.JwtService;

import static daily_farm.api.ApiConstants.*;

import java.util.UUID;

@RestController
@Slf4j
public class CustomerController {

    @Autowired
    ICustomer customerService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private CustomerAuthService authService;
    
    @GetMapping("/test")
    public String test() {
        System.out.println("Test pinged");
        return "OK";
    }
    
  //Registration and verification

    @PostMapping(CUSTOMER_REGISTER)
    public ResponseEntity<String> registerCustomer( @RequestBody CustomerRegistrationDto customerDto) {
        log.info("Registering new customer: " + customerDto.getEmail());
        return customerService.registerCustomer(customerDto);
    }
    
    @GetMapping(CUSTOMER_EMAIL_VERIFICATION)
    public ResponseEntity<String> emailVerification(@RequestParam("token") String token) {
        log.info("Received request to verify email. Token: {}", token);

        if (token == null || token.isEmpty()) {
            log.error("Error: there is no token!");
            return ResponseEntity.badRequest().body("Error: there is no token!");
        }
        return customerService.emailVerification(token);
    }
    
    @GetMapping(CUSTOMER_EMAIL_VERIFICATION_RESEND)
    public ResponseEntity<String> resendVerificationLink(@Valid @RequestBody SendToRequestDto sendToRequestDto) {
        return customerService.resendVerificationLink(sendToRequestDto.getEmail());
    }
    
    //Authorization and logout

    @PostMapping(CUSTOMER_LOGIN)
    public ResponseEntity<TokensResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return customerService.loginCustomer(loginRequestDto);
    }
    
    @DeleteMapping(CUSTOMER_LOGOUT)
    public ResponseEntity<String> logoutCustomer(@RequestParam String token) {
        return customerService.logoutCustomer(token);
    }
    
  //Changing and restoring the password
    
    @PutMapping(CUSTOMER_CHANGE_PASSWORD)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<TokensResponseDto> customerUpdatePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordDto) {
        return customerService.updatePassword(changePasswordDto);
    }
    
    @GetMapping(CUSTOMER_RESET_PASSWORD)
    public ResponseEntity<String> generateAndSendNewPassword(@Valid @RequestBody SendToRequestDto sendToRequestDto) {
        return customerService.generateAndSendNewPassword(sendToRequestDto.getEmail());
    }
    
    //Changing customer's data
    
//    @PutMapping(CUSTOMER_EDIT)
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    public ResponseEntity<String> updateCustomer(@Valid @RequestBody CustomerUpdateDataRequestDto customerDto,
//            @AuthenticationPrincipal UserDetailsWithId user) {
//        return customerService.updateCustomer(user.getId(), customerDto);
//    }
   
    @PutMapping(CUSTOMER_CHANGE_PHONE)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> customerUpdatePhone( @RequestBody CustomerUpdatePhoneRequestDto data) {
    	log.info("Request for phone update");
        return customerService.updatePhone(data);
    }
//
//    @PutMapping(CUSTOMER_CHANGE_FIRST_LAST_NAME)
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    public ResponseEntity<String> clientUpdateName(@Valid @RequestBody FullNameDto fullname,
//            @AuthenticationPrincipal UserDetailsWithId user) {
//        return customerService.changeName(user.getId(), fullname);
//    }
    
    //Deleting an account
    
    @DeleteMapping(CUSTOMER_REMOVE)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> removeCustomer(UUID id) {
        return customerService.removeCustomer(id);
    }
    
  //Email Update


    @PutMapping(CUSTOMER_CHANGE_EMAIL)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> customerUpdateEmail(@RequestParam String token) {
        return customerService.updateEmail(token);
    }
    
 @PreAuthorize("hasRole('ROLE_CUSTOMER')")
 @GetMapping(CUSTOMER_EMAIL_CHANGE_VERIFICATION)
 public ResponseEntity<String> sendVerificationTokenForUpdateEmail(@RequestBody CustomerUpdateEmailRequestDto data) {
     return customerService.sendVerificationTokenForUpdateEmail(data.getId(), data.getEmail());
 }

 @GetMapping(CUSTOMER_NEW_EMAIL_VERIFICATION)
 public ResponseEntity<String> sendVerificationTokenToNewEmail(@RequestParam String token) {
     return customerService.sendVerificationTokenToNewEmail(token);
 }

// Other
	@PostMapping(CUSTOMER_REFRESH_TOKEN)
	public ResponseEntity<RefreshTokenResponseDto> refresh(
	        @RequestBody RefreshTokenRequestDto request) {
	    log.info("Controller: Refresh token starts");
	    return authService.refreshAccessTokenCustomer(request.getRefreshToken());
	}
}
