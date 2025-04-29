package daily_farm.api;


public interface ApiConstants {

	String CUSTOMER_REGISTER = "/customer/register";
	String CUSTOMER_EMAIL_VERIFICATION = "/customer/verify-email";
	String CUSTOMER_EMAIL_VERIFICATION_RESEND = "/customer/verify-email/resend";
	
	String CUSTOMER_EMAIL_CHANGE_VERIFICATION = "/customer/email-update";
	String CUSTOMER_NEW_EMAIL_VERIFICATION = "/customer/verify-newemail";
	
	String CUSTOMER_LOGIN = "/customer/login";
	String CUSTOMER_LOGOUT = "/customer/logout";
	String CUSTOMER_REMOVE = "/customer/";
	String CUSTOMER_REFRESH_TOKEN = "/customer/refresh";
	String CUSTOMER_CHANGE_PASSWORD = "/customer/password";
	String CUSTOMER_RESET_PASSWORD = "/customer/password-reset";
	String CUSTOMER_CHANGE_EMAIL = "/customer/email";
	
	String CUSTOMER_EDIT = "/customer/";
	
	String CUSTOMER_CHANGE_FIRST_LAST_NAME = "/customer/name";
	String CUSTOMER_CHANGE_PHONE = "/customer/phone";


	String CREATE_ORDER = "/customer/order";
}
