package daily_farm.auth.api.dto.farmer_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

import java.util.UUID;

import daily_farm.auth.api.dto.CoordinatesDto;
import daily_farm.auth.api.dto.FarmerRegistrationDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class FarmerRegistrationRequestDto {

	private UUID id;

	private String phone;

	private String company;

	private CoordinatesDto coordinates;
	
	private String language;

//	@NotNull(message = PAYPAL_DETAILS_IS_REQUIRED)
//	@Valid
//	PayPalConfigDto paypalDetails;
//	@Valid
//	AddressDto address;
	
	public static FarmerRegistrationRequestDto of(FarmerRegistrationDto dto, UUID farmerId) {
		return FarmerRegistrationRequestDto.builder()
				.id(farmerId)
				.phone(dto.getPhone())
				.coordinates(dto.getCoordinates())
				.company(dto.getCompany())
				.build();
	}
}
