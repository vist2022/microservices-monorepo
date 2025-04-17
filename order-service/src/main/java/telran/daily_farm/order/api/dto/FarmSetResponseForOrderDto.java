package telran.daily_farm.order.api.dto;


import java.util.UUID;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class FarmSetResponseForOrderDto {


	private UUID farmSetId;
	private UUID farmerId;
	private double price;
	

}
