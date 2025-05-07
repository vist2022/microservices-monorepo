package daily_farm.auth.farmer_auth.service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import daily_farm.auth.api.dto.SendResetPasswordRequestDto;
import daily_farm.auth.api.dto.SendVerificationLinkRequestDto;



@FeignClient(name = "emailService", url = "http://localhost:8087")
public interface EmailServiceClient {

	@PostMapping("/email/email-verification")
    ResponseEntity<Void> sendEmailVerification(@RequestBody SendVerificationLinkRequestDto dto); 
	
	@PostMapping("/email/reset-password")
	ResponseEntity<Void> sendResetPassword(@RequestBody SendResetPasswordRequestDto dto);
}
