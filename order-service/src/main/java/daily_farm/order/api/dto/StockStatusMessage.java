package daily_farm.order.api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockStatusMessage implements Serializable{

	private static final long serialVersionUID = -7534563056193862464L;
	
	@JsonProperty("farmset_id")
    @NotNull
	private String farmsetId;
	
	@JsonProperty("order_id")
	private String orderId;
	
	@JsonProperty("new_stock")
    @NotNull
	private int newStock;
	
	
	
	@JsonProperty("status")
    @NotNull
	private String status;
	
	@JsonProperty("timestamp")
    @NotNull
	private String timestamp;
}
