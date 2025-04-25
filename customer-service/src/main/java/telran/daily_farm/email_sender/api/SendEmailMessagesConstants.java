package telran.daily_farm.email_sender.api;

public interface SendEmailMessagesConstants {
	
	
	String EMAIL_VERIFICATION_SUBJECT =  "Email verification";
	
	String EMAIL_VERIFICATION_HEADER = "Registration Confirmation";
	String EMAIL_VERIFICATION_TEXT = "To complete your registration, click the button below:";
	String EMAIL_VERIFICATION_FOOTER = "If the button does not work, copy and open the following link manually";
	
	String CHANGE_EMAIL_VERIFICATION_HEADER = "Confirmation of email change";
	
	String RESET_PASSWORD_HEADER = "Reset password";
	String RESET_PASSWORD_TEXT = "To login you can use password below:";
	String RESET_PASSWORD_FOOTER = "We strongly recommend changing your password as soon as possible";
	

}
