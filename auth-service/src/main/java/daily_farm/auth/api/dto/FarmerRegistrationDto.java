package daily_farm.auth.api.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import static daily_farm.auth.api.messages.ErrorMessages.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class FarmerRegistrationDto {
	
	@Pattern(regexp = "\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}", message = PHONE_NUMBER_IS_NOT_VALID)
	@NotBlank( message = PHONE_IS_REQUIRED)
	String phone;
	
	@NotBlank( message = EMAIL_IS_NOT_VALID)
	@Email( message = EMAIL_IS_NOT_VALID)
	String email;
	
	@Size(min = 8, message = PASSWORD_IS_NOT_VALID)
	@NotBlank( message = PASSWORD_IS_REQUIRED)
    private String password;
	
//	@Valid
//	AddressDto address;
	
	@NotBlank(message = COMPANY_NAME_IS_REQUIRED)
	String company;

	@Valid
	CoordinatesDto coordinates;

//	@NotNull(message = PAYPAL_DETAILS_IS_REQUIRED)
//	@Valid
//	PayPalConfigDto paypalDetails;

}
