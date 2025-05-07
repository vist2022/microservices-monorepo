package daily_farm.order.controller;


import org.springframework.http.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import daily_farm.order.api.dto.CreateOrderRequestDto;
import daily_farm.order.api.dto.CreateOrderResponseDto;
import daily_farm.order.service.OrderService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import static daily_farm.order.api.OrderServiceApiConstants.*;

import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderServise;

	    
	@PostMapping(CREATE_ORDER)
	public ResponseEntity<CreateOrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto request,
			@RequestHeader("X-User-Id") String customerId,
			@RequestHeader("Authorization") String token
			) {
		
		log.info("new service start");
		CreateOrderResponseDto response = orderServise.createOrder(request.getFarmSetId(), UUID.fromString(customerId)); // , user.getId()
		
		log.info("Order controller: response ( sum - {} , link - {}", response.getSumOfOrder(), response.getPaymentLink());
		
		return  ResponseEntity.ok(response);
	}
	

}
