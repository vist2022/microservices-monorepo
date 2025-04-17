package telran.daily_farm.payment.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.daily_farm.order.api.dto.OrderStatus;
import telran.daily_farm.order.entity.OrderFarmSet;
import telran.daily_farm.payment.entity.Payment;
import telran.daily_farm.payment.paypal.PayPalService;
import telran.daily_farm.payment.repo.PaymentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final PaymentRepository paymentRepo;
	private final PayPalService paypalService;

	public String createPayment(UUID orderId, Double amount, String provider) {
		if ("PayPal".equalsIgnoreCase(provider)) {
			log.info("PaymentService : payment provider - {}", provider);
			return paypalService.getPaypalLink(amount, orderId);
		}
		throw new IllegalArgumentException("Payment provider is not support");
	}

	private boolean capturePayment(String provider, String paymentProviderId) {
		if ("PayPal".equalsIgnoreCase(provider)) {
			return paypalService.isPaid(paymentProviderId);
		}
		throw new IllegalArgumentException("Payment provider is not support");
	}

	@Transactional
	public ResponseEntity<String> handleSuccessPayment(String paymentProviderId) {
		Payment payment = paymentRepo.findPaymentByPaymentProviderId(paymentProviderId);
		log.info("PaymentService: handleSuccessPayment - payment exists in databese");

		if (capturePayment(payment.getProvider(), paymentProviderId)) {
			log.info("PaymentService: handleSuccessPayment  - capturePayment- true");
			OrderFarmSet orderFarmSet = payment.getOrderFarmSet();
			orderFarmSet.setStatus(OrderStatus.PAID);
			log.info("PaymentService: handleSuccessPayment - orderFarmSet status updated to PAID");
		}

		return ResponseEntity.ok("ok");
	}

	@Transactional
	public void cancelPaymentOrder(OrderFarmSet orderFarmSet) {
		log.info("PaymentService: cancelPayment started.");
		
		Payment payment = paymentRepo.findByOrderFarmSet(orderFarmSet)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found"));
		log.info("PaymentService: cancelPayment - payment exsists");
		
//		if(payment.getStatus().equals("COMPLITED")) {
//			log.error("PaymentService: cancelPayment - To make refund payment must be complited and not {}", payment.getStatus());
//			throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "To make refund payment must be complited");
//		}
		
		String paymentProvider = payment.getProvider();
		log.info("PaymentService: cancelPayment - payment provider - {}", paymentProvider);
		if ("PayPal".equalsIgnoreCase(paymentProvider)) {

			 paypalService.cancelPayPalOrder(payment.getPaymentProviderId());
		}
	}
}
