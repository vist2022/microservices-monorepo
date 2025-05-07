package daily_farm.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import daily_farm.api.dto.SendVerificationLinkRequestDto;
import daily_farm.api.dto.SendResetPasswordRequestDto;
import daily_farm.service.MailSenderService;
import daily_farm.service.SendGridEmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EmailSendController {

	private final SendGridEmailSender gridSender;
	private final MailSenderService emailService;
	
	@PostMapping("/email/email-verification")
	public void sendEmailVerification(@RequestBody SendVerificationLinkRequestDto dto) {
		
		log.info("EmailSendController. Request to send link to {}", dto.getEmail());
		
		gridSender.sendEmailVerification(dto.getEmail(), dto.getToken(), dto.getPath());
	}
	
	@PostMapping("/email/reset-password")
	public void  sendResetPassword(@RequestBody SendResetPasswordRequestDto dto) {
		log.info("EmailSendController. Request to send new password to {}", dto.getEmail());
		
		gridSender.sendResetPassword(dto.getEmail(), dto.getPassword());
	}
}
