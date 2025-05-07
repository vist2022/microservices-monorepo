package daily_farm.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SendResetPasswordRequestDto {

	private String email;

	private String password;
}
