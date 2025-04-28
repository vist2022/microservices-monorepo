package telran.daily_farm.email_sender.service;

import org.springframework.http.ResponseEntity;

public interface IMailSender {

	ResponseEntity<String> sendEmailVerification(String email, String verificationToken, String verificationPath);

	ResponseEntity<String> sendChangeEmailVerification(String email, String verificationTokenForUpdateEmail);

	ResponseEntity<String> sendEmail(String email, String subject, String message);

	ResponseEntity<String> sendVerificationTokenToNewEmail(String newEmailFromToken, String token);

	ResponseEntity<String> sendResetPassword(String email, String genPassword);
}
