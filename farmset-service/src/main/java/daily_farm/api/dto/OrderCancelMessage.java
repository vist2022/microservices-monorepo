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
public class OrderCancelMessage implements Serializable{
	

	private static final long serialVersionUID = 8759072702595573453L;
	
	@JsonProperty("order_id")
    @NotNull
    private String orderId;
	

		
}
