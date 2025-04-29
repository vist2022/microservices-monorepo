package telran.daily_farm.email_sender.service;

import static telran.daily_farm.email_sender.api.EmailSenderApiConstants.*;
import static telran.daily_farm.email_sender.api.SendEmailMessagesConstants.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Primary
@Service("SendGridEmailSender")
public class SendGridEmailSender implements IMailSender {

	@Value("${sendgrid.api.key}")
	private String sendgridApiKey;
	
	@Value("${sender.grid.from.email}")
	private String fromEmail;
	
    @Value("${daily.farm.domain}")
    private String domain;
	
	@Override
	public ResponseEntity<String> sendEmailVerification(String email, String verificationToken, String verificationPath) {
		String link = domain + verificationPath + "?token=" + verificationToken;
		String htmlContent = getHtmlContent(link, EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT, EMAIL_VERIFICATION_FOOTER);  
		return sendEmail(email, EMAIL_VERIFICATION_SUBJECT, htmlContent);
	}
	
	@Override
	public ResponseEntity<String> sendChangeEmailVerification(String email, String verificationTokenForUpdateEmail) {
		String link = domain + FARMER_NEW_EMAIL_VERIFICATION+"?token="+ verificationTokenForUpdateEmail;
		String htmlContent = getHtmlContent(link, CHANGE_EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT, EMAIL_VERIFICATION_FOOTER);  
		sendEmail(email, EMAIL_VERIFICATION_SUBJECT, htmlContent);
		
		return ResponseEntity.ok("Check your email for verification");
	}
	
	@Override
	public ResponseEntity<String> sendVerificationTokenToNewEmail(String newEmailFromToken, String token) {
		String link = domain + FARMER_CHANGE_EMAIL + "?token="+ token;
		String htmlContent = getHtmlContent(link, CHANGE_EMAIL_VERIFICATION_HEADER, EMAIL_VERIFICATION_TEXT, EMAIL_VERIFICATION_FOOTER);  
		sendEmail(newEmailFromToken, EMAIL_VERIFICATION_SUBJECT, htmlContent);
		
		return ResponseEntity.ok("Check your email for verification");
		
	}
	
	@Override
	public ResponseEntity<String> sendResetPassword(String email, String genPassword) {
		String htmlContent =  
				 "<!DOCTYPE html>" +
				            "<html>" +
				            "<head><meta charset='UTF-8'></head>" +
				            "<body style='font-family: Arial, sans-serif; text-align: center;'>" +
				            "<h2>"+RESET_PASSWORD_HEADER+"</h2>" +
				            "<p>"+RESET_PASSWORD_TEXT+"</p>" +

				            "<p" +
				            "style='display: inline-block; padding: 12px 24px; font-size: 16px; " +
				            "color: white; background-color: #28a745; text-decoration: none; " +
				            "border-radius: 5px; font-weight: bold;'>"+ genPassword +"</a>" +

				            "<p style='font-size: 16px;'>"+RESET_PASSWORD_FOOTER+"</p>" +
				           
				            "</body></html>";

		sendEmail(email, RESET_PASSWORD_HEADER, htmlContent);
		
		return ResponseEntity.ok("Check your email for new password");
		
	}

	private String getHtmlContent(String link, String header, String text, String footer) {
		
		return  "<!DOCTYPE html>" +
	            "<html>" +
	            "<head><meta charset='UTF-8'></head>" +
	            "<body style='font-family: Arial, sans-serif; text-align: center;'>" +
	            "<h2>" + header + "</h2>" +
	            "<p> " + text + "</p>" +

	            "<a href='" + link + "'" +
	            "style='display: inline-block; padding: 12px 24px; font-size: 16px; " +
	            "color: white; background-color: #28a745; text-decoration: none; " +
	            "border-radius: 5px; font-weight: bold;'>Confirm Email</a>" +

	            "<p>" + footer + "</p>" +
	            "<p style='font-size: 8px;><a href='" + link + "'>" + link + "</a></p>" +
	            "</body></html>";
	}

	@Override
	public ResponseEntity<String> sendEmail(String email, String subject, String message) {
		Email from = new Email(fromEmail);
		
	    Email to = new Email(email);
	    Content content = new Content("text/html", message);
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid(sendgridApiKey);
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());
	      
	      
	    } catch (IOException ex) {
	    	return ResponseEntity.ok("server error");
	    }
		return ResponseEntity.ok("Check your email for verification");
	}

}
