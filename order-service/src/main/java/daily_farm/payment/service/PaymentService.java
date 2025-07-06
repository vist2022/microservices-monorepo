package daily_farm.payment.service;

import java.awt.TrayIcon.MessageType;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import daily_farm.order.entity.OrderFarmSet;
import daily_farm.order.api.dto.OrderCancelMessage;
import daily_farm.order.api.dto.OrderCreatedMessage;
import daily_farm.order.api.dto.OrderRequestMessage;
import daily_farm.order.api.dto.OrderStatus;
import daily_farm.order.api.dto.PaymentRequestMessage;
import daily_farm.order.api.dto.PaymentStatusMessage;
import daily_farm.payment.entity.Payment;
import daily_farm.payment.paypal.PayPalService;
import daily_farm.payment.repo.PaymentRepository;
import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepo;
	private final PayPalService paypalService;
	private final ObjectMapper objectMapper;
	private final StreamBridge streamBridge;
	private final OrderFeignClient orderFeignClient;

	public void createPayment(PaymentRequestMessage message) {
		
		log.info("PaymentService. Create payment for order - {}", message.getOrderId());
		
		
			if ("PayPal".equalsIgnoreCase(message.getProvider())) {
				log.info("PaymentService : payment provider - {}", message.getProvider());
				paypalService.createPaypalLink(message);
			}
		//TODO  ===>>>>	//throw new IllegalArgumentException("Payment provider is not support");
	}

//	private boolean isOrderCreated(String orderId) {
//		log.info("PaymentService. isOrderCreated  - {}", orderId);
//		//TODO if order not exists - rest 
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		OrderStatus status = paymentRepo.findByOrderId(UUID.fromString(orderId)).get().getOrderStatus();
//		
//		return status.equals(OrderStatus.WAITING_FOR_PAYMENT);
//	}

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
			sendPaymentStatusMessage(payment.getOrderId(), OrderStatus.PAID);
			log.info("PaymentService: handleSuccessPayment - orderFarmSet status updated to PAID");
		}

		return ResponseEntity.ok("ok");
	}

	private void sendPaymentStatusMessage(UUID orderId, OrderStatus paid) {
		PaymentStatusMessage message = new PaymentStatusMessage(orderId.toString(), paid);
		String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException er) {
        	log.error("OrderService. Order Cancel Serialization error");
            throw new RuntimeException("Serialization error", er);
        }

        try {
            streamBridge.send("paymentStatus-out-0", messageJson);
        } catch (Exception er) {
        	
            throw new RuntimeException("Kafka sending message error", er);
        }
		
		
	}

	@Transactional
	public void updateOrderStatus(OrderCreatedMessage message) {
		log.error("PaymentService. updateOrderStatus - order status before- {}", message.getOrderId());

		Payment payment = paymentRepo.findByOrderId(UUID.fromString(message.getOrderId())).get();
		log.error("PaymentService. updateOrderStatus - order status before- {}", payment.getOrderStatus());
		payment.setOrderStatus(message.getOrderStatus());
		log.error("PaymentService. updateOrderStatus - order status after - {}", payment.getOrderStatus());
		paymentRepo.save(payment);

	}

	public String getPaymentLink(String orderId) {
		Optional<Payment> optionalPayment;
	    int maxRetries = 5;
	    int delayMs = 1000; 

	    for (int attempt = 1; attempt <= maxRetries; attempt++) {
	        optionalPayment = paymentRepo.findByOrderId(UUID.fromString(orderId));

	        if (optionalPayment.isPresent() && optionalPayment.get().getOrderStatus().equals(OrderStatus.WAITING_FOR_PAYMENT) && optionalPayment.get().getPaymentLink() != null) {
	            String link = optionalPayment.get().getPaymentLink();
	            log.info("PaymentService. link for order - {} is {}", orderId, link);
	            return link;
	        }

	        log.info("PaymentService. Waiting for payment link, attempt {}/{}", attempt, maxRetries);
	        try {
	            Thread.sleep(delayMs);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            log.error("PaymentService. Retry interrupted for order {}", orderId);
	            return null;
	        }
	    }

	    log.error("PaymentService. getPaymentLink. Order is not exists or not in WAITING_FOR_PAYMENT after retries");
	    sendCancelOrderMessage(orderId);
	    
	    return null;
		
	}

	private void sendCancelOrderMessage(String orderId) {
	
			OrderCancelMessage message = new OrderCancelMessage(orderId);
			String messageJson;
	        try {
	            messageJson = objectMapper.writeValueAsString(message);
	        } catch (JsonProcessingException er) {
	        	log.error("OrderService. Order Cancel Serialization error");
	            throw new RuntimeException("Serialization error", er);
	        }

	        try {
	            streamBridge.send("orderCancel-out-0", messageJson);
	        } catch (Exception er) {
	        	
	            throw new RuntimeException("Kafka sending message error", er);
	        }
			
		}
		
}

	
	//TODO   cancelPaymentOrder
	
//	@Transactional
//	public void cancelPaymentOrder(OrderFarmSet orderFarmSet) {
//		log.info("PaymentService: cancelPayment started.");
//		
//		Payment payment = paymentRepo.findByOrderFarmSet(orderFarmSet)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found"));
//		log.info("PaymentService: cancelPayment - payment exsists");
//		
////		if(payment.getStatus().equals("COMPLITED")) {
////			log.error("PaymentService: cancelPayment - To make refund payment must be complited and not {}", payment.getStatus());
////			throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "To make refund payment must be complited");
////		}
//		
//		String paymentProvider = payment.getProvider();
//		log.info("PaymentService: cancelPayment - payment provider - {}", paymentProvider);
//		if ("PayPal".equalsIgnoreCase(paymentProvider)) {
//
//			 paypalService.cancelPayPalOrder(payment.getPaymentProviderId());
//		}
//	}

