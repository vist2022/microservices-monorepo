package daily_farm.payment.test_config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import daily_farm.order.entity.OrderFarmSet;
import daily_farm.payment.service.PaymentService;

@Configuration
@Profile("load-test")
public class LoadTestConfig {

//    @Bean
//     PaymentService paymentService() {
//        return new PaymentService() {
//            @Override
//            public String createPayment(UUID orderId, Double amount, String provider)  {
//                return "mock-payment-link";
//            }
//
//            @Override
//            public void cancelPaymentOrder(OrderFarmSet order) {
//                // Пустая реализация
//            }
//        };
//    }
}
