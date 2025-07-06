package daily_farm.payment.service;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import daily_farm.order.api.dto.OrderCreatedMessage;
import daily_farm.order.api.dto.PaymentRequestMessage;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class PaymentConsumerService {
	
	@Autowired 
	private PaymentService paymentService;
	
	
	@Bean
    Consumer<PaymentRequestMessage> paymentCreate() {
        return message -> {
            log.error("PaymentConsumerService. Receives message - paymentCreate : {}", message);
            try {
            	paymentService.createPayment(message);
            } catch (Exception e) {
                log.error("PaymentConsumerService. Error processing paymentCreate: {}", message, e);
            }
        };
    }
	
	@Bean
    Consumer<OrderCreatedMessage> orderCreated() {
        return message -> {
            log.error("PaymentConsumerService. Receives message - orderCreated : {}", message);
            try {
            	paymentService.updateOrderStatus(message);
            } catch (Exception e) {
                log.error("PaymentConsumerService. Error processing orderCreated: {}", message, e);
            }
        };
    }

}
