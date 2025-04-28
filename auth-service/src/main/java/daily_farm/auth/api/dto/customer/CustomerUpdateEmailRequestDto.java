package daily_farm.auth.api.dto.customer;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerUpdateEmailRequestDto {
    
	@NotBlank
	private UUID id;

    private String email;
}
