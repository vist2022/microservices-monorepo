package daily_farm.security.api;

public interface AuthApiConstants {

	String FARMER_REGISTER = "/farmer/register";
	String FARMER_EMAIL_VERIFICATION = "/farmer/verify-email";
	String FARMER_EMAIL_VERIFICATION_RESEND = "/farmer/verify-email/resend";

	String FARMER_EMAIL_CHANGE_VERIFICATION = "/farmer/email-update";
	String FARMER_NEW_EMAIL_VERIFICATION = "/farmer/verify-newemail";

	String FARMER_LOGIN = "/farmer/login";
	String FARMER_LOGOUT = "/farmer/logout";

	String FARMER_REFRESH_TOKEN = "/farmer/refresh";
	String FARMER_CHANGE_PASSWORD = "/farmer/password";
	String RESET_PASSWORD = "/farmer/password-reset";
	String FARMER_CHANGE_EMAIL = "/farmer/email";
	String FARMER_REMOVE = "/farmer/";
	String GET_ALL_SETS = "/farm-sets";
	
	String GET_LANGUAGES = "/languages";
	
	String GET_CATEGORIES = "/categories";
	String GET_SIZES = "/sizes";
	
	
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
