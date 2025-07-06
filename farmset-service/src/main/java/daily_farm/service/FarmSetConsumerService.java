package daily_farm.service;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import daily_farm.api.dto.OrderCancelMessage;
import daily_farm.api.dto.OrderFailedMessage;
import daily_farm.api.dto.StockUpdateMessage;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FarmSetConsumerService {

	@Autowired
	private FarmSetService service;
	@Bean
    Consumer<OrderFailedMessage> orderFailed() {
        return message -> {
            log.info("FarmSetService. Received message in topic orderFailed : {}", message);
            try {
            	service.cancelReserve(message);
            } catch (Exception e) {
                log.error("Error processing message: {}", message, e);
              
            }
        };
    }
	

	
}
