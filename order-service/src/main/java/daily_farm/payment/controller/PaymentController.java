package daily_farm.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import daily_farm.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
	
	private final PaymentService paymentService;
	
	
	@GetMapping("/paypal/success")
	public ResponseEntity<String> handleSuccess(@RequestParam("token") String paymentProviderId) {
		log.info("PaymentController: handleSuccess");
		paymentService.handleSuccessPayment(paymentProviderId);
        	
        return ResponseEntity.ok("ok");
    }

}
