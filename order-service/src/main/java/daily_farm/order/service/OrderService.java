package daily_farm.order.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import daily_farm.order.entity.OrderFarmSet;
import daily_farm.order.api.dto.OrderCancelMessage;
import daily_farm.order.api.dto.OrderCreatedMessage;
import daily_farm.order.api.dto.OrderFailedMessage;
import daily_farm.order.api.dto.OrderRequestMessage;
import daily_farm.order.api.dto.OrderStatus;
import daily_farm.order.api.dto.PaymentStatusMessage;
import daily_farm.order.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.stream.function.StreamBridge;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

	//private final FarmSetFeignClient farmSetClient;
	private final OrderRepository orderRepo;
	//private final PaymentService paymentService;

	//private final RedisTemplate<String, FarmSetResponseForOrderDto> redisTemplate;
	//private final RedisTemplate<String, Integer> stockRedisTemplate;
	private final ObjectMapper objectMapper;
	private final StreamBridge streamBridge;
	
	public void orderCreate(OrderRequestMessage message) {
		
		try {
			orderRepo.save(OrderFarmSet.builder()
					.id(UUID.fromString(message.getOrderId()))
					.farmSetId(UUID.fromString(message.getFarmsetId()))
					.customerId(UUID.fromString(message.getCustomerId()))
					.farmerId(UUID.fromString(message.getFarmerId()))
					.status(OrderStatus.WAITING_FOR_PAYMENT)
					.createdAt(LocalDateTime.now())
					.sumOfOrder(message.getSum())
					.build());
			
			log.info("OrderService : New order {} saved to database with status WAITING_FOR_PAYMENT", message.getOrderId());
			
			Thread.sleep(5000);
			
			sendOrderCreatedMessage(message);
		} catch (Exception e) {
			log.error("OrderService. orderCreate for order {} error", message.getOrderId());
			sendOrderFailedMessage(message.getOrderId(), message.getFarmsetId());
			
		}		
	}
	
	private void sendOrderCreatedMessage(OrderRequestMessage message) {
		OrderCreatedMessage orderCreatedMessage = new OrderCreatedMessage(message.getOrderId(), OrderStatus.WAITING_FOR_PAYMENT);
		String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(orderCreatedMessage);
        } catch (JsonProcessingException er) {
        	log.error("OrderService. Order Create Serialization error");
            throw new RuntimeException("Serialization error", er);
        }

        try {
            streamBridge.send("orderCreated-out-0", messageJson);
        } catch (Exception er) {
        	
            throw new RuntimeException("Kafka sending message error", er);
        }
		
	}

	private void sendOrderFailedMessage(String orderId, String farmsetId) {
		OrderFailedMessage orderFailedMessage = new OrderFailedMessage(orderId, farmsetId, LocalDateTime.now().toString());
		String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(orderFailedMessage);
        } catch (JsonProcessingException er) {
        	log.error("OrderService. Order Create Serialization error");
            throw new RuntimeException("Serialization error", er);
        }

        try {
            streamBridge.send("orderFailed-out-0", messageJson);
        } catch (Exception er) {
        	
            throw new RuntimeException("Kafka sending message error", er);
        }
	}
	
	

	@Override
	@Scheduled(fixedRate = 60000)
	public void checkPendingPayments() {
//		LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(1);
//		List<OrderFarmSet> expiredOrders = orderRepo.findExpiredOrders(tenMinutesAgo, PageRequest.of(0, 100));
//		log.error("OrderService : Checking payment....", expiredOrders.size());
//		
//		for (OrderFarmSet order : expiredOrders) {
//			log.info("Cancel order {} — not paid", order.getId());
//			cancelOrder(order);
//
//		}
	}
	
	@Override
	@Transactional
	public void cancelOrder(OrderFarmSet order) {
	
//		OrderFarmSet orderToCancel = orderRepo.findById(order.getId()).get();
//	
//		log.info("cancelOrder: orderid - {}", orderToCancel.getId());
//	
//	
//	
//	
//		log.info("OrderService : Cancel order - add count to farmset");
//	
//		// paymentService.cancelPaymentOrder(order);
//	
//		orderToCancel.setStatus(OrderStatus.CANCELLED);
//		orderRepo.save(orderToCancel);
	
	}

	
	@Transactional
	public void orderCancel(OrderCancelMessage message) {
		UUID orderId = UUID.fromString(message.getOrderId());
		
		Optional<OrderFarmSet> optionalOrder = orderRepo.findById(orderId);
		
		if(optionalOrder.isPresent()) {
			OrderFarmSet order =  optionalOrder.get();
			order.setStatus(OrderStatus.CANCELLED);
			sendOrderFailedMessage(order.getId().toString(), order.getFarmSetId().toString());
		}
		
		log.info("Order Service. Order - {} canceled", orderId);
		
	}

	@Transactional
	public void updateOrderStatus(PaymentStatusMessage message) {
		OrderFarmSet order = orderRepo.findById(UUID.fromString(message.getOrderId())).get();
		
		order.setStatus(message.getOrderStatus());
		orderRepo.save(order);
		
	}

//	public PaymentRequestMessage isOrderWaitingForPayment(UUID orderId) {
//		Optional<OrderFarmSet> optionalOrder = orderRepo.findById(orderId);
//		PaymentRequestMessage response;
//		if(optionalOrder.isPresent()) {
//			OrderFarmSet order = optionalOrder.get();
//			 response = PaymentRequestMessage.builder()
//					 .provider(dto.getProvider())
//						.customerId(id)
//						.farmerId(farmSet.getFarmerId().toString())
//						.farmsetId(farmSet.getId().toString())
//						.orderId(orderID.toString())
//						.orderStatus(OrderStatus.NEW)
//						.amount(farmSet.getPrice())
//					.build();
//		}
//
//		log.info("OrderService. isOrderWaitingForPayment({}) - ", orderId, res);
//		
//		return res;
//	}
}


//@Override
//@Transactional
//public String createOrder(UUID farmsetId, UUID customerId) {
//	log.info("OrderService: Create order request");
//
//	FarmSetResponseForOrderDto farmSetResponse = getFarmSetForOrder(farmsetId);
//	
//	OrderFarmSet orderFarmSet = OrderFarmSet.builder().farmSetId(farmsetId).customerId(customerId)
//			.farmerId(farmSetResponse.getFarmerId()).status(OrderStatus.WAITING_FOR_PAYMENT)
//			.createdAt(LocalDateTime.now()).sumOfOrder(farmSetResponse.getPrice()).build();
//
//	orderRepo.save(orderFarmSet);
//	
//	log.info("OrderService : New order saved to database");		
//
//	if (!decreaseCachedStok(farmsetId)) {
//		log.error("OrderService. Failed to decrease stock in cache: {}", farmsetId);
//	}
//
//	decreaseStok(farmsetId, orderFarmSet.getId());
//	
//	return orderFarmSet.getId().toString();
//	
//}
//
//public void processOrder(StockStatusMessage message) {
//	
//	CreateOrderResponseDto response;
//	
//	UUID farmsetId;
//	UUID orderId;
//    try {
//        farmsetId = UUID.fromString(message.getFarmsetId());
//    } catch (IllegalArgumentException e) {
//        log.error("Invalid farmsetId: {}", message.getFarmsetId());
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid farmsetId: {}");
//    }
//    
//    try {
//        orderId = UUID.fromString(message.getOrderId());
//    } catch (IllegalArgumentException e) {
//        log.error("Invalid orderId: {}", message.getOrderId());
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid orderId: {}");
//    }
//	
//    
//    String redisKey = "stock:" + farmsetId;
//    stockRedisTemplate.opsForValue().set(redisKey, message.getNewStock());
//    log.info("Updated cache for farmsetId: {}, availible count: {}", farmsetId, message.getNewStock());
//    
//    
//    
//	OrderFarmSet orderFarmSet = orderRepo.findById(orderId)
//			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not exists"));
//	
//	orderFarmSet.setStatus(message.getStatus().equals("SUCCESS") ? OrderStatus.WAITING_FOR_PAYMENT : OrderStatus.CANCELLED);
//	log.info("OrderService : Order status updated");
//
//	
//	if(orderFarmSet.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)) {
//		String approvalLink = paymentService.createPayment(orderFarmSet.getId(), orderFarmSet.getSumOfOrder(),
//				"PayPal");
//		log.info("OrderService : Create order - link for payment generated - {} ", approvalLink);
//
//		response = new CreateOrderResponseDto(farmsetId, orderFarmSet.getSumOfOrder(), "WAITING_FOR_PAYMENT", approvalLink);
//		log.info("OrderService : Create order - Response for customer created!");
//		
//	}else {
//		log.error("OrderService : Order status - {}", OrderStatus.CANCELLED);
//		response = new CreateOrderResponseDto(farmsetId, 0 , "CANCELLED", null);
//	}
//	
//	messagingTemplate.convertAndSend("/topic/orders/" + orderId, response);
//
//}
//
//private void decreaseStok(UUID farmSetId, UUID orderId) {
//	StockUpdateMessage message = new StockUpdateMessage(farmSetId.toString(), orderId.toString(), -1, Instant.now().toString());
//	
//	String messageJson;
//    try {
//        messageJson = objectMapper.writeValueAsString(message);
//    } catch (JsonProcessingException e) {
//    	increaseCachedStok(farmSetId); 
//        throw new RuntimeException("Serialization error", e);
//    }
//
// 
//    try {
//        streamBridge.send("stockUpdates-out-0", messageJson);
//    } catch (Exception e) {
//    	increaseCachedStok(farmSetId); 
//        throw new RuntimeException("Kafka sending message error", e);
//    }
//	
//	
//}
//
//private boolean decreaseCachedStok(UUID farmSetId) {
//
//	String key = "stock:" + farmSetId;
//	String script = "local stock = redis.call('GET', KEYS[1]); " + "if not stock then return -1; end; "
//			+ "stock = tonumber(stock); " + "if stock <= 0 then return -1; end; "
//			+ "redis.call('DECR', KEYS[1]); return stock - 1;";
//	RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
//
//	Instant start = Instant.now();
//
//	Long result = redisTemplate.execute(redisScript, Collections.singletonList(key));
//
//	long durationMs = Duration.between(start, Instant.now()).toMillis();
//	log.info("decreaseCachedStok: Redis script took {} ms for key {}", durationMs, key);
//
//	if (result == null || result < 0) {
//		log.warn("decreaseCachedStok: Not enough stock for {}", farmSetId);
//		return false;
//	}
//	return true;
//
//}
//
//private FarmSetResponseForOrderDto getFarmSetForOrder(UUID farmsetId) {
//	Instant start = Instant.now();
//    String farmsetKey = "farmset:" + farmsetId;
//    String stockKey = "stock:" + farmsetId;
//    String lockKey = "lock:farmset:" + farmsetId;
//
//    FarmSetResponseForOrderDto farmSetForOrder = (FarmSetResponseForOrderDto) redisTemplate.opsForValue().get(farmsetKey);
//    if (farmSetForOrder != null) {
//        Instant end = Instant.now();
//        long durationMs = Duration.between(start, end).toMillis();
//        log.info("Odrer Service. FarmSetForOrder reseived from cache in {} ms for farmSetId: {}", durationMs,
//                farmSetForOrder.getFarmSetId());
//        return farmSetForOrder;
//    }
//
//    Boolean acquiredLock = false;
//    try {
//        while (!acquiredLock) {
//            acquiredLock = redisTemplate.opsForValue().setIfAbsent(lockKey, farmSetForOrder, Duration.ofSeconds(10));
//            if (acquiredLock) {
//                farmSetForOrder = (FarmSetResponseForOrderDto) redisTemplate.opsForValue().get(farmsetKey);
//                if (farmSetForOrder != null) {
//                    Instant end = Instant.now();
//                    long durationMs = Duration.between(start, end).toMillis();
//                    log.info("Odrer Service. FarmSetForOrder reseived from cache after lock in {} ms for farmSetId: {}", durationMs,
//                            farmSetForOrder.getFarmSetId());
//                    return farmSetForOrder;
//                }
//
//                try {
//                    log.info("OrderService. CreateOrder. farmsetId - {}", farmsetId);
//                    farmSetForOrder = farmSetClient.getFarmSetResponseForOrder(farmsetId);
//                    Instant end = Instant.now();
//                    long durationMs = Duration.between(start, end).toMillis();
//                    log.info("Odrer Service. FarmSetForOrder reseived from farmset service completed in {} ms for farmSetId: {}",
//                            durationMs, farmSetForOrder.getFarmSetId());
//                } catch (FeignException e) {
//                    log.error("Failed to receive data: {}", e.getMessage());
//                    throw new RuntimeException("Failed to receive data: " + e.getMessage());
//                }
//
//                log.info("Odrer Service. FarmSetForOrder reseived from database");
//                redisTemplate.opsForValue().set(farmsetKey, farmSetForOrder, Duration.ofMinutes(5));
//                stockRedisTemplate.opsForValue().set(stockKey, farmSetForOrder.getAvailibleCount(), Duration.ofMinutes(5));
//                log.info("Odrer Service. FarmSetForOrder added to cache");
//                return farmSetForOrder;
//            } else {
//                Thread.sleep(100); 
//            }
//        }
//    } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//        throw new RuntimeException( e);
//    } finally {
//        if (acquiredLock) {
//            redisTemplate.delete(lockKey);
//        }
//    }
//    return farmSetForOrder; 
//}
//@Override
//@Transactional
//public void cancelOrder(OrderFarmSet order) {
//
//	OrderFarmSet orderToCancel = orderRepo.findById(order.getId()).get();
//
//	log.info("cancelOrder: orderid - {}", orderToCancel.getId());
//
//	increaseCachedStok(order.getFarmSetId());
//
//	// increaseStok(order.getFarmSetId());
//	log.info("OrderService : Cancel order - add count to farmset");
//
//	// paymentService.cancelPaymentOrder(order);
//
//	orderToCancel.setStatus(OrderStatus.CANCELLED);
//	orderRepo.save(orderToCancel);
//
//}
//
//private void increaseCachedStok(UUID farmSetId) {
//	String key = "stock:" + farmSetId;
//	// TODO - if key not existts????
//	Long result = redisTemplate.opsForValue().increment(key, 1);
//	log.info("increaseCachedStok: Success. Remaining stock: {}", result);
//}
//
//private void increaseStok(UUID farmSetId) {
//
//	try {
//		farmSetClient.increaseStock(new FarmSetRequestForCancelOrderDto(farmSetId));
//	} catch (FeignException.BadRequest e) {
//		log.warn("Failed to increase stock due to bad request: {}", e.getMessage());
//		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel order: " + e.getMessage(), e);
//	} catch (FeignException.NotFound e) {
//		log.warn("FarmSet not found: {}", e.getMessage());
//		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FarmSet not found", e);
//	} catch (FeignException e) {
//		e.printStackTrace();
//		throw new RuntimeException("Failed to increase stock: " + e.getMessage());
//	}
//
//}





