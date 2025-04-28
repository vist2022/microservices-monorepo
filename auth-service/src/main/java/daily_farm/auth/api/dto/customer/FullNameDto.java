package daily_farm.auth.api.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static daily_farm.auth.api.messages.ErrorMessages.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FullNameDto {

	@NotBlank
	@Pattern(regexp = "[A-Z][a-z]{1,20}([-\\s][A-Z][a-z]{1,20})*", message = NAME_IS_NOT_VALID)
	String firstName;

	@NotBlank
	@Pattern(regexp = "[A-Z][a-z]{1,20}([-\\s][A-Z][a-z]{1,20})*", message = LAST_NAME_IS_NOT_VALID)
	String lastName;
}
