package telran.daily_farm.order.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import telran.daily_farm.order.api.dto.FarmSetRequestForCancelOrderDto;
import telran.daily_farm.order.api.dto.FarmSetRequestForOrderDto;
import telran.daily_farm.order.api.dto.FarmSetResponseForOrderDto;


@FeignClient(name = "farmSetService", url = "http://localhost:8080/farm-set")
public interface FarmSetFeignClient {

	@PutMapping("/order")
	FarmSetResponseForOrderDto decreaseStock(@RequestBody FarmSetRequestForOrderDto request);
	
	@PutMapping("/cancel_order")
	void increaseStock(@RequestBody FarmSetRequestForCancelOrderDto request);
}
