package daily_farm.payment.service;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import daily_farm.order.api.dto.PaymentRequestMessage;


@FeignClient(name = "orderService", url = "http://localhost:8086/order/")
public interface OrderFeignClient {

	@GetMapping("/status")
	PaymentRequestMessage isOrderWaitingForPayment(@RequestParam  UUID orderId);
}
