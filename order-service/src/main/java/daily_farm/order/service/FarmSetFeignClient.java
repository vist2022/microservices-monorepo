package daily_farm.order.service;


import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import daily_farm.order.api.dto.FarmSetRequestForCancelOrderDto;
import daily_farm.order.api.dto.FarmSetResponseForOrderDto;


@FeignClient(name = "farmSetService", url = "http://localhost:8085/farm-set")
public interface FarmSetFeignClient {

//	@PutMapping("/order")
//	FarmSetResponseForOrderDto decreaseStock(@RequestBody FarmSetRequestForOrderDto request);
	
	@GetMapping("/set-for-order")
	FarmSetResponseForOrderDto getFarmSetResponseForOrder(@RequestParam  UUID farmSetId);
	
	@PutMapping("/cancel_order")
	void increaseStock(@RequestBody FarmSetRequestForCancelOrderDto request);
}
