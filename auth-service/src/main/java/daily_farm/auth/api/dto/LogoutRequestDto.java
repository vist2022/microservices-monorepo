package daily_farm.auth.api.dto;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LogoutRequestDto {
	
	@NotBlank
	private UUID id;
	
}
