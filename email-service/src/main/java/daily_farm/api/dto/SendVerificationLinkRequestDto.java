package daily_farm.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SendVerificationLinkRequestDto {

	private String email;
	
	private String token;
	
	private String path;
	
	
	
}
