package daily_farm.order.service;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import daily_farm.order.api.dto.OrderCancelMessage;
import daily_farm.order.api.dto.OrderRequestMessage;
import daily_farm.order.api.dto.PaymentStatusMessage;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OrderConsumerService {
	
	@Autowired 
	private OrderService orderService;
	
	
	
	@Bean
    Consumer<OrderRequestMessage> orderCreate() {
        return message -> {
            log.error("OrderConsumerService. Receives message - order create : {}", message);
            try {
                orderService.orderCreate(message);
            
            } catch (Exception e) {
                log.error("OrderConsumerService. Error processing order create: {}", message, e);
            }
        };
    }
	
	@Bean
    Consumer<OrderCancelMessage> orderCancel() {
        return message -> {
            log.error("OrderConsumerService. Receives message - order cancel : {}", message);
            try {
                orderService.orderCancel(message);
            
            } catch (Exception e) {
                log.error("OrderConsumerService. Error processing order cancel: {}", message, e);
            }
        };
    }
	
	@Bean
    Consumer<PaymentStatusMessage> paymentStatus() {
        return message -> {
            log.error("OrderConsumerService. Receives message -  payment status : {}", message);
            try {
                orderService.updateOrderStatus(message);
            
            } catch (Exception e) {
                log.error("OrderConsumerService. Error processing payment status: {}", message, e);
            }
        };
    }

}
