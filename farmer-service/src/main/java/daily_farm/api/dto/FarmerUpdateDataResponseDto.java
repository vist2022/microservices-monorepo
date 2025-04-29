package daily_farm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmerUpdateDataResponseDto {

	private String accessToken ;
	
    private String refreshToken ;
	
}
