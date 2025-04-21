package daily_farm.security.api.dto;

import static daily_farm.security.api.messages.ErrorMessages.*;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CoordinatesDto {

	@NotNull(message = LATITUDE_REQUIRED)
	@Min(value = -90, message = INVALID_LATITUDE)
	@Max(value = 90, message = INVALID_LATITUDE)
	private Double latitude;

	@NotNull(message = LONGITUDE_REQUIRED)
	@Min(value = -180, message = INVALID_LONGITUDE)
	@Max(value = 180, message = INVALID_LONGITUDE)
	private Double longitude;

}
