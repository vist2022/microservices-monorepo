package daily_farm.order.api.dto;



import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateOrderResponseDto {
	
	private UUID orderId;
	
	private double sumOfOrder;
	
	private String message;

	private String paymentLink;

}
