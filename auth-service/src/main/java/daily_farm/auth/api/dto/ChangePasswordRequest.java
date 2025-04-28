package daily_farm.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static daily_farm.auth.api.messages.ErrorMessages.*;


@NoArgsConstructor
@Getter
public class ChangePasswordRequest {


	@NotBlank(message = OLD_NEW_PASSWORD_REQUARED)
	@Size(min = 8, message = PASSWORD_IS_NOT_VALID)
	String oldPassword;
	
	@Size(min = 8, message = PASSWORD_IS_NOT_VALID)
	@NotBlank(message = OLD_NEW_PASSWORD_REQUARED)
	String newPassword;
}
