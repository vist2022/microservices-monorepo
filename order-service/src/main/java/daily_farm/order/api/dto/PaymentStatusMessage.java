package daily_farm.order.api.dto;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentStatusMessage implements Serializable{
	

	private static final long serialVersionUID = 6164023202188356467L;

	@JsonProperty("order_id")
    @NotNull
    private String orderId;
	
	@JsonProperty("order_status")
    @NotNull
    private OrderStatus orderStatus;
	
		
}
