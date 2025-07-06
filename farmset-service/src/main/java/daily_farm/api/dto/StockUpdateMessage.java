package daily_farm.api.dto;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockUpdateMessage implements Serializable{
	

	private static final long serialVersionUID = 3492611623201504873L;


	@JsonProperty("farmset_id")
    @NotNull
    private String farmsetId;
	
	
	@JsonProperty("order_id")
    @NotNull
	private String orderId;


    @JsonProperty("quantity_change")
    @Min(-1)
    @Max(1)
    private int quantityChange;

    @JsonProperty("timestamp")
    @NotNull
    private String timestamp;

}
