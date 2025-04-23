package telran.daily_farm.email_sender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import static telran.daily_farm.email_sender.api.EmailSenderApiConstants.*;
import static telran.daily_farm.email_sender.api.SendEmailMessagesConstants.*;

@Service("mailSenderService")
@Slf4j
public class MailSenderService implements IMailSender {

	@Autowired
	JavaMailSender sender;

	@Value("${daily.farm.domain}")
	private String domain;
//    http://localhost:8080
//    https://daily-farm-latest.onrender.com

	@Value("${sender.grid.from.email}")
	private String fromEmail;

	@Override
	public ResponseEntity<String> sendEmailVerification(String email, String verificationToken,
			String verificationPath) {
		String link = domain + verificationPath + "?token=" + verificationToken;
		String htmlContent = getHtmlContent(link, EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT,
				EMAIL_VERIFICATION_FOOTER);
		return sendEmail(email, EMAIL_VERIFICATION_SUBJECT, htmlContent);

	}

	@Override
	public ResponseEntity<String> sendEmail(String email, String subject, String message) {
		MimeMessage msg = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setFrom(fromEmail);
			helper.setText(message, true);
			sender.send(msg);
		} catch (MessagingException e) {
			return ResponseEntity.ok("Error with sending error");
		}

		return ResponseEntity.ok("Check your email for verification");

	}

	public ResponseEntity<String> sendResetPassword(String email, String genPassword) {
		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head><meta charset='UTF-8'></head>"
				+ "<body style='font-family: Arial, sans-serif; text-align: center;'>" + "<h2>" + RESET_PASSWORD_HEADER
				+ "</h2>" + "<p>" + RESET_PASSWORD_TEXT + "</p>" +

				"<p" + "style='display: inline-block; padding: 12px 24px; font-size: 16px; "
				+ "color: white; background-color: #28a745; text-decoration: none; "
				+ "border-radius: 5px; font-weight: bold;'>" + genPassword + "</a>" +

				"<p style='font-size: 16px;'>" + RESET_PASSWORD_FOOTER + "</p>" +

				"</body></html>";

		sendEmail(email, RESET_PASSWORD_HEADER, htmlContent);

		return ResponseEntity.ok("Check your email for new password");

	}

	public ResponseEntity<String> sendChangeEmailVerification(String email, String verificationTokenForUpdateEmail) {
		String link = domain + FARMER_NEW_EMAIL_VERIFICATION + "?token=" + verificationTokenForUpdateEmail;
		String htmlContent = getHtmlContent(link, CHANGE_EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT,
				EMAIL_VERIFICATION_FOOTER);
		sendEmail(email, EMAIL_VERIFICATION_SUBJECT, htmlContent);

		return ResponseEntity.ok("Check your email for verification");
	}

	public ResponseEntity<String> sendVerificationTokenToNewEmail(String newEmailFromToken, String token) {
		String link = domain + FARMER_CHANGE_EMAIL + "?token=" + token;
		String htmlContent = getHtmlContent(link, CHANGE_EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT,
				EMAIL_VERIFICATION_FOOTER);
		sendEmail(newEmailFromToken, "Email verification", htmlContent);
		return ResponseEntity.ok("Check your email for verification");

	}

	private String getHtmlContent(String link, String header, String text, String footer) {

		return "<!DOCTYPE html>" + "<html>" + "<head><meta charset='UTF-8'></head>"
				+ "<body style='font-family: Arial, sans-serif; text-align: center;'>" + "<h2>" + header + "</h2>"
				+ "<p> " + text + "</p>" +

				"<a href='" + link + "'" + "style='display: inline-block; padding: 12px 24px; font-size: 16px; "
				+ "color: white; background-color: #28a745; text-decoration: none; "
				+ "border-radius: 5px; font-weight: bold;'>Confirm Email</a>" +

				"<p>" + footer + "</p>" + "<p style='font-size: 8px;><a href='" + link + "'>" + link + "</a></p>"
				+ "</body></html>";
	}
}
