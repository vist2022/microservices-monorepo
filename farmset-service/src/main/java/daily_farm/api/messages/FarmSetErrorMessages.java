package daily_farm.api.messages;

public interface FarmSetErrorMessages {
	
	String SIZE_NOT_NULL_VALUE = "Size can not be empty";
	
	String CATEGORY_NOT_NULL_VALUE = "Category can not be empty";
	
	String LONG_DESCRIPTION = "Description is too long (max length = 200 symbols)";
	
	String PRICE_IS_NOT_POSITIVE = "Price must be positive";
	
	String COUNT_IS_NOT_POSITIVE = "Amount must be positive";
	
	String DATE_NOT_NULL_VALUE = "Date can not be empty";
	
	String WRONG_DATES = "pickupTimeEnd must be after pickupTimeStart";
	
	String SIZE_IS_NOT_AVAILABLE = "Entered size is not available";
	
	String CATEGORY_IS_NOT_AVAILABLE = "Entered Category is not available";

}
