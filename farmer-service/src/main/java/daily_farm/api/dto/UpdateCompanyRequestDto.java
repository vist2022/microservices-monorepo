package daily_farm.api.dto;

import static daily_farm.api.messages.ErrorMessages.COMPANY_NAME_IS_REQUIRED;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCompanyRequestDto {
	
	@NotBlank(message = COMPANY_NAME_IS_REQUIRED)
	private String company;

}
