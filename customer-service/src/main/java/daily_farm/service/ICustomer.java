package daily_farm.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import daily_farm.api.dto.*;
import daily_farm.entity.Customer;

public interface ICustomer {
	// Registration and verification
	ResponseEntity<String> registerCustomer(@Valid CustomerRegistrationDto customerDto);// +

	ResponseEntity<String> emailVerification(String verificationToken);// +

	ResponseEntity<String> resendVerificationLink(@Valid String email);// +
	// Authorization and logout

	ResponseEntity<TokensResponseDto> loginCustomer(@Valid LoginRequestDto loginRequestDto);// +

	ResponseEntity<String> logoutCustomer(String token);// +
	// Changing and restoring the password

	ResponseEntity<TokensResponseDto> updatePassword(@Valid ChangePasswordRequest changePasswordDto);// +

	ResponseEntity<String> generateAndSendNewPassword(@Valid String email);// +
	// Updating the customer's data

	ResponseEntity<String> updateCustomer(UUID id, CustomerUpdateDataRequestDto customerDto);// +

	ResponseEntity<String> updatePhone(CustomerUpdatePhoneRequestDto data);// +

	ResponseEntity<String> changeName(UUID uuid, @Valid FullNameDto fullname);// +
	// Deleting an account

	ResponseEntity<String> removeCustomer(UUID id);// +
	// Email Update

	ResponseEntity<String> sendVerificationTokenForUpdateEmail(UUID id, @Valid String newEmail);// +

	ResponseEntity<String> sendVerificationTokenToNewEmail(String token);

	ResponseEntity<String> updateEmail(@Valid String verificationToken);

	Customer getCustomer(UUID customerId);

}
