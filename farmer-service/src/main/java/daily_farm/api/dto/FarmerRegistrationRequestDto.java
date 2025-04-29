package daily_farm.api.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import static daily_farm.api.messages.ErrorMessages.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class FarmerRegistrationRequestDto {
	
	@NotNull
	UUID id;
	
	@Pattern(regexp = "\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}", message = PHONE_NUMBER_IS_NOT_VALID)
	@NotBlank( message = PHONE_IS_REQUIRED)
	String phone;
	
	@NotBlank(message = COMPANY_NAME_IS_REQUIRED)
	String company;

	@Valid
	@NotNull
	CoordinatesDto coordinates;
	
	@Valid
	String language;

//	@NotNull(message = PAYPAL_DETAILS_IS_REQUIRED)
//	@Valid
//	PayPalConfigDto paypalDetails;
//	@Valid
//	AddressDto address;
}
