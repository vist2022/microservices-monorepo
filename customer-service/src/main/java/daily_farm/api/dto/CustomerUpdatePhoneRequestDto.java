package daily_farm.api.dto;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerUpdatePhoneRequestDto {
    
	@NotBlank
	private UUID id;

    private String newPhone;


}
