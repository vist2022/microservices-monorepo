package telran.daily_farm.order.api.dto;



import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateOrderRequestDto {
	
	@NotBlank
	private UUID farmSetId;
	
	@NotBlank
	private UUID customerId;
}
