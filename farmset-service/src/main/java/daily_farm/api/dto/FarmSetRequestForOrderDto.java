package daily_farm.api.dto;


import java.util.UUID;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FarmSetRequestForOrderDto {


	private UUID farmSetId;
	
	private String provider;
	
}
