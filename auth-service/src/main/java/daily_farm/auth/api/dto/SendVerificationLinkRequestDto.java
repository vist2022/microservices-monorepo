package daily_farm.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class SendVerificationLinkRequestDto {

	private String email;
	
	private String token;
	
	private String path;
	
	
	
}
