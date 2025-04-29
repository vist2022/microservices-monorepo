package daily_farm.security.api;

import static daily_farm.security.api.AuthApiConstants.*;
import java.util.ArrayList;
import java.util.List;



public class PulicEndpoints {
	
	private static final List<String> PUBLIC_ENDPOINTS =List.of(
	        FARMER_REFRESH_TOKEN,
	        FARMER_LOGIN,
	        FARMER_EMAIL_VERIFICATION,
	        FARMER_EMAIL_VERIFICATION_RESEND,
	        FARMER_CHANGE_EMAIL,
	        FARMER_NEW_EMAIL_VERIFICATION,
	        FARMER_REGISTER,
	        
	        RESET_PASSWORD,

	        CUSTOMER_REFRESH_TOKEN,
	        CUSTOMER_EMAIL_VERIFICATION,
	        CUSTOMER_EMAIL_VERIFICATION_RESEND,
	        CUSTOMER_RESET_PASSWORD,
	        CUSTOMER_CHANGE_EMAIL,
	        CUSTOMER_NEW_EMAIL_VERIFICATION,
	        CUSTOMER_REGISTER,
	        CUSTOMER_LOGIN,
	        "/farm-set/order",
	        "/farm-set/cancel_order"

	    );
	    
	    private static final String PAYPAL_PREFIX = "/paypal";
	    
	    private void PulicEndpoints() {
	        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	    }
	    
	    public static boolean isPublicEndpoint(String requestURI) {
	        return PUBLIC_ENDPOINTS.stream().anyMatch(uri -> uri.equals(requestURI)) || 
	               requestURI.startsWith(PAYPAL_PREFIX);
	    }


}
