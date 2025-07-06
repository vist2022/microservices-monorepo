package daily_farm.api.dto;


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
public class OrderCreatedMessage implements Serializable{
	

	private static final long serialVersionUID = 1799884890840593932L;

	@JsonProperty("order_id")
    @NotNull
    private String orderId;
	
	@JsonProperty("order_status")
    @NotNull
    private OrderStatus orderStatus;
	
		
}
