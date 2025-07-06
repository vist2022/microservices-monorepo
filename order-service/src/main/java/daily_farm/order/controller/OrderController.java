package daily_farm.order.controller;


import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import daily_farm.order.api.dto.CreateOrderRequestDto;
import daily_farm.order.api.dto.CreateOrderResponseDto;
import daily_farm.order.api.dto.PaymentRequestMessage;
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

	    
//	@PostMapping(CREATE_ORDER)
//	public ResponseEntity<CreateOrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto request,
//			@RequestHeader("X-User-Id") String customerId,
//			@RequestHeader("Authorization") String token
//			) {
//		
//		log.info("new service start");
//		 String orderId = orderServise.createOrder(request.getFarmSetId(), UUID.fromString(customerId)); 
//		
//		return ResponseEntity.accepted().body(new CreateOrderResponseDto(UUID.fromString(orderId), 0, "PENDING",  null));
//	}
	
//	@GetMapping("order/status")
//	public ResponseEntity<PaymentRequestMessage> isOrderWaitingForPayment(@RequestParam UUID orderId){
//		log.info("OrderController. Request fot order - {}  status", orderId);
//		boolean isWaitingForPayment = orderServise.isOrderWaitingForPayment(orderId);
//		return ResponseEntity.ok(isWaitingForPayment);
//	}
	

}
