package telran.daily_farm.order.api.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateOrderResponseDto {
	
	private double sumOfOrder;
	
	private String message;

	private String paymentLink;

}
