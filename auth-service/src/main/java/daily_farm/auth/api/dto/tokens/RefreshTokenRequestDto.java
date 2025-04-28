package daily_farm.auth.api.dto.tokens;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefreshTokenRequestDto {

	@NotBlank
	String refreshToken;
}
