package daily_farm.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;
import daily_farm.entity.Customer;
import daily_farm.entity.CustomerCredential;
import daily_farm.repo.CustomerCredentialRepository;
import daily_farm.repo.CustomerRepository;
import daily_farm.email_sender.service.IMailSender;
import daily_farm.email_sender.service.MailSenderService;
import daily_farm.api.dto.TokensResponseDto;
import daily_farm.customer_auth.CustomerAuthService;
import daily_farm.token.JwtService;
import daily_farm.token.TokenBlacklistService;

import static daily_farm.api.messages.ErrorMessages.*;
import static daily_farm.api.ApiConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService implements ICustomer {

    private final CustomerRepository customerRepo;
    private final CustomerCredentialRepository credentialRepo;
    private final PasswordEncoder passwordEncoder;
    private final CustomerAuthService authService;
    private final IMailSender mailSender;
    private final JwtService jwtService; 
    private final TokenBlacklistService blackListService; 

  //Registration and verification
    
    @Override
    @Transactional
    public ResponseEntity<String> registerCustomer(CustomerRegistrationDto customerDto) {
        log.info("Service. Registering customer - " + customerDto.getEmail());

        checkEmailIsUnique(customerDto.getEmail());
        log.info("Service. Email is unique");
        
        Customer customer = Customer.of(customerDto);
        log.debug("Service. Created Entity customer from DTO");
        
        customerRepo.save(customer);
        log.info("Service. Customer saved to database");
        customerRepo.flush();

        CustomerCredential credential = CustomerCredential.builder()
                .createdAt(LocalDateTime.now())
                .passwordLastUpdated(LocalDateTime.now())
                .hashedPassword(passwordEncoder.encode(customerDto.getPassword()))
                .customer(customer)
                .build();
        credentialRepo.save(credential);
        log.info("Serviсe. Customer credentials saved successfully");
        
        String email = customerDto.getEmail();
        mailSender.sendEmailVerification(email, 
                jwtService.generateVerificationToken(customer.getId().toString(), email), CUSTOMER_EMAIL_VERIFICATION);
            log.info("Servise. Email verification sent");
        
        return ResponseEntity.ok("Customer registered successfully. Please check your email for verification.");
    }
    
    @Override
    @Transactional
    public ResponseEntity<String> emailVerification(String verificationToken) {
        log.info("Service.emailVerification - Request received. Token: {}", verificationToken);

        try {
            
            String emailFromToken = jwtService.extractUserEmail(verificationToken);
            log.info("Service.emailVerification - Extracted email from token: {}", emailFromToken);

           
            boolean isTokenValid = jwtService.isTokenValid(verificationToken, emailFromToken);
            boolean isTokenExpired = jwtService.isTokenExpired(verificationToken);
            boolean isTokenBlacklisted = blackListService.isBlacklisted(verificationToken);

            log.info("Service.emailVerification - Token valid: {}, Token expired: {}, Token blacklisted: {}",
                     isTokenValid, isTokenExpired, isTokenBlacklisted);

            
            if (!isTokenValid || isTokenExpired || isTokenBlacklisted) {
                log.error("Service.emailVerification - Invalid token for email: {}", emailFromToken);
                throw new JwtException(INVALID_TOKEN);
            }

            
            Customer customer = customerRepo.findByEmail(emailFromToken)
                    .orElseThrow(() -> {
                        log.error("Service.emailVerification - No customer found for email: {}", emailFromToken);
                        return new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_IS_NOT_EXISTS);
                    });

            log.info("Service.emailVerification - Customer found: {}", customer.getEmail());

            
            CustomerCredential credential = credentialRepo.findByCustomer(customer);
            if (credential == null) {
                log.error("Service.emailVerification - No credentials found for customer: {}", customer.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No credentials found for customer");
            }

            
            credential.setVerificated(true);
            log.info("Service.emailVerification - Customer verification updated: {}", customer.getEmail());

            
            blackListService.addToBlacklist(verificationToken);
            log.info("Service.emailVerification - Token blacklisted: {}", verificationToken);

            return ResponseEntity.ok(EMAIL_IS_VERIFICATED);

        } catch (JwtException e) {
            log.error("Service.emailVerification - JWT Exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service.emailVerification - Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during verification");
        }
    }

    
    @Override
    @Transactional
    public ResponseEntity<String> resendVerificationLink(String email) { 
    	log.info("Service.resendVerificationLink. Request to resend verification email for: " + email);
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
        log.info("Service.resendVerificationLink. Customer exists - " + email);

        mailSender.sendEmailVerification(email, 
                jwtService.generateVerificationToken(customer.getId().toString(), email), CUSTOMER_EMAIL_VERIFICATION);

        log.info("Service.resendVerificationLink. Verification link sent - " + email);
        
        return ResponseEntity.ok("Verification link sent. Please check your email.");
    }
    
    //Authorization and logout

    @Override
    @Transactional
    public ResponseEntity<TokensResponseDto> loginCustomer(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        log.debug("Service. Request to login customer -" + email);

        Customer customer = customerRepo.findByEmail(email).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));

        if (!credentialRepo.findByCustomer(customer).isVerificated()) {
            log.debug("Service. Login. Email is not verificated. Send link to email -" + email);
            
            mailSender.sendEmailVerification(email,
                    jwtService.generateVerificationToken(customer.getId().toString(), email), CUSTOMER_EMAIL_VERIFICATION);

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, EMAIL_IS_NOT_VERIFICATED);
        }

        TokensResponseDto tokens = authService.authenticateCustomer(email, loginRequestDto.getPassword());
        log.debug("Service. Login successful, token returned to user");
        return ResponseEntity.ok(tokens);
    }

    @Override
    @Transactional
    public ResponseEntity<String> logoutCustomer(String token) {
        log.info("Service.logoutCustomer Logout starts");

        Customer customer = customerRepo.findById(UUID.fromString(jwtService.extractUserId(token)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        CustomerCredential credential = credentialRepo.findByCustomer(customer);

        log.info("Service.logoutCustomer Got credential - refresh token");
        log.info("Service.logoutCustomer Got access token");

        token = token.substring(7); 
        blackListService.addToBlacklist(token);
        log.info("Service.logoutCustomer AccessToken added to black list");

        credential.setRefreshToken("");
        log.info("Service.logoutCustomer RefreshToken removed from credential");

        return ResponseEntity.ok("Customer logged out successfully");
    }

  //Changing and restoring the password
    @Override
    @Transactional
    public ResponseEntity<TokensResponseDto> updatePassword(ChangePasswordRequest changePasswordDto) {
        Customer customer = customerRepo.findById(changePasswordDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        CustomerCredential credential = credentialRepo.findByCustomer(customer);
        
        String oldPassword = credential.getHashedPassword();
        log.info("Service.updatePassword. Old password is - " + oldPassword);

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), oldPassword))
            throw new IllegalArgumentException("Invalid old password");
        log.info("Service.updatePassword. Old password matches, updating...");

        credential.setHashedPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        credential.setPasswordLastUpdated(LocalDateTime.now());
        log.info("Service.updatePassword. New password set for customer - " + customer.getEmail());
        
        TokensResponseDto tokens = authService.authenticateCustomer(customer.getEmail(), changePasswordDto.getNewPassword());

        return ResponseEntity.ok(tokens);
    }

    @Override
    @Transactional
    public ResponseEntity<String> generateAndSendNewPassword(String email) {
    	log.info("Service.generateAndSendNewPassword(). Request to reset password for: " + email);
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
        log.info("Service.generateAndSendNewPassword(). Customer exists - " + email);

        CustomerCredential credential = credentialRepo.findByCustomer(customer);
        String newPassword = jwtService.generatePassword(10);
        credential.setHashedPassword(passwordEncoder.encode(newPassword));
        log.info("Service.generateAndSendNewPassword(). Password hashed and saved to credential");
        credential.setPasswordLastUpdated(LocalDateTime.now());

        mailSender.sendResetPassword(email, newPassword);
        log.info("Service.generateAndSendNewPassword(). New password was sent to email");
        
        return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK);
    }
    
  //Updating the customer's data
    
    @Override
    @Transactional
    public ResponseEntity<String> updateCustomer(UUID id, CustomerUpdateDataRequestDto customerDto) {
    	log.info("Service.updateCustomer(). Update customer data starts");
    	Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

       customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        log.info("Service.updateCustomer(). Name updated");
        customer.setPhone(customerDto.getPhone());
        log.info("Service.updateCustomer(). Phone updated");
        return ResponseEntity.ok("Customer data updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<String> updatePhone(CustomerUpdatePhoneRequestDto data) {
    
    	log.info("Service.updatePhone(). Request to update phone for customer - " + data.getId());
        Customer customer = customerRepo.findById(data.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    
        customer.setPhone(data.getNewPhone());
        log.info("Service.updatePhone(). Phone updated successfully for customer - " + data.getId());
        return ResponseEntity.ok("Phone updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<String> changeName(UUID id, FullNameDto fullname) {
    	log.info("Service.changeName(). Request to update name for customer - " + id);
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    
        customer.setFirstName(fullname.getFirstName());
        customer.setLastName(fullname.getLastName());
        log.info("Service.changeName(). Name updated successfully for customer - " + id);
        return ResponseEntity.ok("First name and last name updated successfully");
    }
    
  //Deleting an account
        
    @Override
    @Transactional
    public ResponseEntity<String> removeCustomer(UUID id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        customerRepo.delete(customer);
        return ResponseEntity.ok("Customer removed successfully");
    }

    //Email Update
    
    private void checkEmailIsUnique(String email) {
    	log.info("Service.checkEmailIsUnique(). Checking email - " + email);
        if (customerRepo.existsByEmail(email)) {
            log.warn("Service.checkEmailIsUnique(). Email already exists - " + email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_EXISTS);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> sendVerificationTokenForUpdateEmail(UUID id, String newEmail) { 
    	log.info("Service.sendVerificationTokenForUpdateEmail(). Request to update email for customer - " + id);
    	 if (customerRepo.findByEmail(newEmail).isPresent()) {
    	        log.warn("Service.sendVerificationTokenForUpdateEmail(). Email already exists - " + newEmail);
    	        throw new ResponseStatusException(HttpStatus.CONFLICT, CUSTOMER_WITH_THIS_EMAIL_EXISTS);
    	    }

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        String email = customer.getEmail();
        String token = jwtService.generateVerificationTokenForUpdateEmail(customer.getId().toString(), email, newEmail);
        
        mailSender.sendChangeEmailVerification(email, token);
        log.info("Service.sendVerificationTokenForUpdateEmail(). Verification link sent to email - " + email);
        return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK + " - " + email);
    }
    
    @Override
    public ResponseEntity<String> sendVerificationTokenToNewEmail(String verificationToken) {
        log.info("Service.sendVerificationTokenToNewEmail(). Processing request...");

        String newEmailFromToken = jwtService.extractUserNewEmail(verificationToken);
        String oldEmailFromToken = jwtService.extractUserEmail(verificationToken);

        if (jwtService.isTokenValid(verificationToken, oldEmailFromToken)
                && !blackListService.isBlacklisted(verificationToken)) {
            
            String id = jwtService.extractUserId(verificationToken);
            String newToken = jwtService.generateVerificationTokenForUpdateEmail(id, oldEmailFromToken, newEmailFromToken);

            mailSender.sendVerificationTokenToNewEmail(newEmailFromToken, newToken);
            log.info("Service.sendVerificationTokenToNewEmail(). Token sent to new email - " + newEmailFromToken);

            blackListService.addToBlacklist(verificationToken);
            log.info("Service.sendVerificationTokenToNewEmail(). Old token added to blacklist.");
        } else {
            throw new JwtException(INVALID_TOKEN);
        }
        
        return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK + " - " + newEmailFromToken);
    }
    
    @Override
    @Transactional
    public ResponseEntity<String> updateEmail(String verificationToken) { 
    	log.info("Service.updateEmail(). Request to update email");
        try {
            String oldEmailFromToken = jwtService.extractUserEmail(verificationToken);
            String newEmailFromToken = jwtService.extractUserNewEmail(verificationToken);
            
            log.info("Service.updateEmail(). Old email from token - " + oldEmailFromToken);
            log.info("Service.updateEmail(). New email from token - " + newEmailFromToken);

            if (jwtService.isTokenValid(verificationToken, oldEmailFromToken) && !blackListService.isBlacklisted(verificationToken)) {
                Customer customer = customerRepo.findByEmail(oldEmailFromToken)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));

                if (customerRepo.findByEmail(newEmailFromToken).isPresent()) {
                	log.warn("Service.updateEmail(). Email already exists - " + newEmailFromToken);
                    throw new ResponseStatusException(HttpStatus.CONFLICT,  CUSTOMER_WITH_THIS_EMAIL_EXISTS);
                }
                    log.info("Service.updateEmail(). Customer exists");
                customer.setEmail(newEmailFromToken);
                log.info("Service.updateEmail(). New email saved - " + newEmailFromToken); 
                blackListService.addToBlacklist(verificationToken);
                log.info("Service.updateEmail(). Token added to blacklist"); 
            } else {
                throw new JwtException(INVALID_TOKEN);
            }
        } catch (Exception e) {
        	 log.error("Service.updateEmail(). Invalid token - " + INVALID_TOKEN);
            throw new JwtException(INVALID_TOKEN);
        }
        return ResponseEntity.ok(EMAIL_IS_VERIFICATED);
    }

    @Override
	public Customer getCustomer(UUID customerId) {
		
		return customerRepo.findById(customerId).get();
	}
}
