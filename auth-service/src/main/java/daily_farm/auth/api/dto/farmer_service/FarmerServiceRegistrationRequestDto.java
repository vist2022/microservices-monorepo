package daily_farm.auth.api.dto.farmer_service;


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

import java.util.UUID;

import daily_farm.auth.api.dto.CoordinatesDto;
import daily_farm.auth.api.dto.FarmerRegistrationDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class FarmerServiceRegistrationRequestDto {
	
	@NotBlank
	private UUID farmerId;
	
	@Pattern(regexp = "\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}", message = PHONE_NUMBER_IS_NOT_VALID)
	@NotBlank( message = PHONE_IS_REQUIRED)
	String phone;
	
	
//	@Valid
//	AddressDto address;
	
	@NotBlank(message = COMPANY_NAME_IS_REQUIRED)
	String company;

	@Valid
	CoordinatesDto coordinates;

//	@NotNull(message = PAYPAL_DETAILS_IS_REQUIRED)
//	@Valid
//	PayPalConfigDto paypalDetails;

	public static FarmerServiceRegistrationRequestDto of(FarmerRegistrationDto dto, UUID farmerId) {
		return FarmerServiceRegistrationRequestDto.builder()
				.farmerId(farmerId)
				.phone(dto.getPhone())
				.coordinates(dto.getCoordinates())
				.company(dto.getCompany())
				.build();
	}
}
