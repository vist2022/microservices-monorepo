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
public class OrderRequestMessage implements Serializable{
	

	private static final long serialVersionUID = 3492611623201504873L;


	@JsonProperty("order_id")
    @NotNull
    private String orderId;
	
	
	@JsonProperty("farmset_id")
    @NotNull
    private String farmsetId;
	

	@JsonProperty("farmer_id")
    @NotNull
    private String farmerId;
	

	@JsonProperty("customer_id")
    @NotNull
    private String customerId;
	
	@JsonProperty("sum")
    @NotNull
	private double sum;
	
	
    @JsonProperty("timestamp")
    @NotNull
    private String timestamp;

}
