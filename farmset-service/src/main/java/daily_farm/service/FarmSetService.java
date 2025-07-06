package daily_farm.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;
import daily_farm.entity.*;
import daily_farm.repo.*;
import jakarta.validation.Valid;

import static daily_farm.api.messages.FarmSetErrorMessages.*;

@Service
@Slf4j
@AllArgsConstructor
public class FarmSetService implements IFarmSetService {

	private final FarmSetSizeRepository sizeRepo;
	private final FarmSetCategoryRepository categoryRepo;
	private final FarmSetRepository farmSetRepo;
	private final StreamBridge streamBridge;

	private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
	@Override
	@Transactional
	public ResponseEntity<Void> addFarmSet(UUID id, FarmSetDto farmSetDto) {
		log.info("FarmSetService. Adding farm-set");

		String size = farmSetDto.getSize();
		FarmSetSize farmSetSize = sizeRepo.findBySize(size)
				.orElseThrow(() -> new IllegalArgumentException(SIZE_IS_NOT_AVAILABLE));
		log.info("FarmSetService. Size from dto is valid");

		String category = farmSetDto.getCategory();
		FarmSetCategory farmSetCategory = categoryRepo.findByCategory(category)
				.orElseThrow(() -> new IllegalArgumentException(CATEGORY_IS_NOT_AVAILABLE));
		log.info("FarmSetService. Category from dto is valid");

		FarmSet farmSet = FarmSet.builder().availibleCount(farmSetDto.getAvailibleCount())
				.description(farmSetDto.getDescription()).price(farmSetDto.getPrice()).farmerId(id)
				.category(farmSetCategory).size(farmSetSize).pickupTimeEnd(farmSetDto.getPickupTimeEnd())
				.pickupTimeStart(farmSetDto.getPickupTimeStart()).build();
		log.info("FarmSetService. Farm-set created");

		farmSetRepo.save(farmSet);
		log.info("FarmSetService. Farm-set saved to data base");

		sizeRepo.findBySize(size).get().getFarmSets().add(farmSet);

		categoryRepo.findByCategory(farmSetDto.getCategory()).get().getFarmSets().add(farmSet);

		return ResponseEntity.ok().build();
	}

	@Override
	@Transactional
	public ResponseEntity<List<FarmSetResponseDto>> getAbailableFarmSetsForFarmer(UUID id) {

		List<FarmSetResponseDto> list = farmSetRepo.findByFarmerId(id).stream().map(fs -> FarmSet.buildFromEntity(fs))
				.toList();
		return ResponseEntity.ok(list);
	}

	@Override
	public ResponseEntity<List<FarmSetResponseDto>> getAllFarmSets() {

		List<FarmSetResponseDto> list = farmSetRepo.findAll().stream().map(fs -> FarmSet.buildFromEntity(fs)).toList();

		// list.forEach(fs -> translateService.translateDto(fs, "zh"));

		return ResponseEntity.ok(list);
	}

//	@Override
//	@Transactional
//	public ResponseEntity<FarmSetResponseForOrderDto> decreaseStock(FarmSetRequestForOrderDto farmSetRequestDto) {
//		Instant start = Instant.now();
////        log.info("Starting decreaseStock for farmSetId: {}", farmSetRequestDto.getFarmSetId());
//		FarmSet farmSet = farmSetRepo.findById(farmSetRequestDto.getFarmSetId())
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm Set is not availible"));
//		log.info("FarmSetService: decreaseStock. farmset exists - {}", farmSet.getDescription());
//
////		if (farmSet.getAvailibleCount() >= 0)
////			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You ara late. Farm Set is not availible");
////		log.info("FarmSetService: decreaseStock. farmset availible - {}", farmSet.getAvailibleCount());
//
////		farmSet.setAvailibleCount(farmSet.getAvailibleCount() - 1);
////		log.info("FarmSetService: decreaseStock. farmset availible after decrease - {}",  farmSet.getAvailibleCount());
////		farmSetRepo.saveAndFlush(farmSet);
////	    log.info("FarmSetService: Saved farmSet with availibleCount = {}", farmSet.getAvailibleCount());
//		int updatedRows = farmSetRepo.decreaseIfAvailable(farmSet.getId());
//		if (updatedRows == 0) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No stock available");
//		}
//		Instant end = Instant.now();
//		long durationMs = java.time.Duration.between(start, end).toMillis();
//		log.info("decreaseStock completed in {} ms for farmSetId: {}", durationMs, farmSetRequestDto.getFarmSetId());
//		return ResponseEntity.ok(FarmSetResponseForOrderDto.fromFarmSet(farmSet));
//	}

	@Override
	public ResponseEntity<Void> increaseStock(FarmSetRequestForCancelOrderDto farmSetRequestDto) {
		log.info("FarmSetService: increaseStock.Recived FarmSetRequestForCancelOrderDto farmsetId - {}",
				farmSetRequestDto.getFarmSetId());
		FarmSet farmSet = farmSetRepo.findById(farmSetRequestDto.getFarmSetId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Farm Set is not availible"));
		log.info("FarmSetService: increaseStock. farmset availible - {}", farmSet.getAvailibleCount());
		farmSet.setAvailibleCount(farmSet.getAvailibleCount() + 1);
		log.info("FarmSetService: increaseStock. farmset availible after increase - {}", farmSet.getAvailibleCount());
		farmSetRepo.save(farmSet);
		farmSetRepo.flush();
		log.info("FarmSetService: increaseStock. Saved farmSet with availibleCount = {}", farmSet.getAvailibleCount());
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<FarmSetResponseForOrderDto> getFarmSet(UUID farmSetId) {

		FarmSet farmSet = farmSetRepo.findById(farmSetId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm Set is not availible"));
		log.info("FarmSetService: getFarmSet. Farmset exists - {}", farmSet.getDescription());

		if (farmSet.getAvailibleCount() <= 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"FarmSet Service. getFarmSet : Farm Set is not available");
		log.info("FarmSetService:  getFarmSet. Farmset exists - {}", farmSet.getAvailibleCount());

		return ResponseEntity.ok(FarmSetResponseForOrderDto.fromFarmSet(farmSet));

	}

	@Transactional
	public void updateStock(StockUpdateMessage message) throws JsonProcessingException {
		String farmsetId = message.getFarmsetId();
		String orderId = message.getOrderId();
		UUID farmsetUUID;
		try {

			farmsetUUID = UUID.fromString(message.getFarmsetId());
		} catch (IllegalArgumentException e) {
			log.error("FarmSet Service. Update stock. Invalid farmsetId: {}", message.getFarmsetId());
			StockStatusMessage status = new StockStatusMessage(message.getFarmsetId(), message.getOrderId(), 0, "FAILED", Instant.now().toString());
			sendStockStatusMessage(status);
			log.info("FarmSet Service. Update stock. StockStatusMessage sended: {}", status);
			return;
		}

		farmSetRepo.findById(UUID.fromString(farmsetId)).ifPresentOrElse(farmset -> {
			int newCount = farmset.getAvailibleCount() + message.getQuantityChange();
			if (newCount < 0) {
				log.warn("FarmSet Service. Update stock. Farmset with id {} is unavailable: {}", farmsetId);
				StockStatusMessage status = new StockStatusMessage(farmsetId, orderId, farmset.getAvailibleCount(),"FAILED", Instant.now().toString());
				sendStockStatusMessage(status);
				log.info("FarmSet Service. Update stock. StockStatusMessage with status {} sended to OrderService", status.getStatus());
				return;
			}

			farmset.setAvailibleCount(newCount);
			farmSetRepo.save(farmset);
			log.info("Stock for farmset {} updated, availible count: {}", farmsetId, newCount);

			StockStatusMessage status = new StockStatusMessage(farmsetId, orderId, newCount, "SUCCESS", Instant.now().toString());
			sendStockStatusMessage(status);
			log.info("FarmSet Service. Update stock. StockStatusMessage with status {} and count {} sended to OrderService", status.getStatus(), status.getNewStock());
		}, () -> {
			log.error("Farmset not found: {}", farmsetId);
			StockStatusMessage status = new StockStatusMessage(farmsetId, orderId, 0, "FAILED", Instant.now().toString());
			sendStockStatusMessage(status);
			log.info("FarmSet Service. Update stock. StockStatusMessage with status {} sended to OrderService", status.getStatus());
		});



	}

	private void sendStockStatusMessage(StockStatusMessage status) {
		String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(status);
        } catch (JsonProcessingException e) {
        	log.error("Serialization error");
            throw new RuntimeException("Serialization error", e);
        }
        
        try {
        	 streamBridge.send("stockStatus-out-0",messageJson);
        } catch (Exception e) {
        	log.error("Kafka sending message error ");
            throw new RuntimeException("Kafka sending message error", e);
        }
		
        
	}

	private void sendOrderRequestMessage(OrderRequestMessage orderRequestMessage) {
		
		long start = System.nanoTime();
        try {
            String messageJson = objectMapper.writeValueAsString(orderRequestMessage);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("order-create", messageJson);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent to order-create, offset: {}, time: {} ms",
                        result.getRecordMetadata().offset(),
                        (System.nanoTime() - start) / 1_000_000);
                } else {
                    log.error("Kafka sending error for order-create: {}, time: {} ms",
                        ex.getMessage(), (System.nanoTime() - start) / 1_000_000);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Serialization error for order-create: {}", e.getMessage(), e);
            throw new RuntimeException("Serialization error", e);
        }
		
//		String messageJson;
//        try {
//            messageJson = objectMapper.writeValueAsString(status);
//        } catch (JsonProcessingException e) {
//        	log.error("Serialization error");
//            throw new RuntimeException("Serialization error", e);
//        }
//        
//        try {
//        	 streamBridge.send("orderCreate-out-0",messageJson);
//        } catch (Exception e) {
//        	log.error("Kafka sending message error ");
//            throw new RuntimeException("Kafka sending message error", e);
//        }
//		
        
	}
	@Override
	@Transactional
	public ResponseEntity<String> reserveFarmSet(FarmSetRequestForOrderDto dto, String id) {
    log.info("Starting reserveFarmSet for farmSetId: {}", dto.getFarmSetId());
    
	FarmSet  farmSet = decreaseStock(dto); 
	
	UUID orderID = UUID.randomUUID();
	log.info("FarmSetService: reserveFarmSet. Generated new order ID - {}", orderID);
	
		
	OrderRequestMessage orderRequestMessage = OrderRequestMessage.builder()
			.customerId(id)
			.farmerId(farmSet.getFarmerId().toString())
			.farmsetId(farmSet.getId().toString())
			.orderId(orderID.toString())
			.sum(farmSet.getPrice())
			.timestamp( Instant.now().toString())
			.build();
	log.info("FarmSetService: reserveFarmSet. Generated OrderRequestMessage");
	sendOrderRequestMessage(orderRequestMessage);
	
	PaymentRequestMessage paymentRequestMessage = PaymentRequestMessage.builder()
			.provider(dto.getProvider())
			.customerId(id)
			.farmerId(farmSet.getFarmerId().toString())
			.farmsetId(farmSet.getId().toString())
			.orderId(orderID.toString())
			.orderStatus(OrderStatus.NEW)
			.amount(farmSet.getPrice())
			.build();
	sendRequestPaymentMessage(paymentRequestMessage);
	log.info("FarmSetService: reserveFarmSet. Send PaymentRequestMessage");
	
	return ResponseEntity.ok(orderID.toString());
	

	}
	
	
	private FarmSet decreaseStock(FarmSetRequestForOrderDto dto) {
		FarmSet farmSet = farmSetRepo.findByIdForUpdate(dto.getFarmSetId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm Set is not found"));
		log.info("FarmSetService: reserveFarmSet. farmset exists - {}", farmSet.getId());

		if (farmSet.getAvailibleCount() <= 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FarmSetService. reserveFarmSet. Farm Set is not availible");
		log.info("FarmSetService: reserveFarmSet. farmset availible - {}", farmSet.getAvailibleCount());

		farmSet.setAvailibleCount(farmSet.getAvailibleCount() - 1);
		log.info("FarmSetService: reserveFarmSet. farmset availible after decrease - {}",  farmSet.getAvailibleCount());
		farmSetRepo.save(farmSet);
		return farmSet;
	}
	
	

	private void sendRequestPaymentMessage(PaymentRequestMessage paymentRequestMessage) {
//		String messageJson;
//        try {
//            messageJson = objectMapper.writeValueAsString(paymentRequestMessage);
//        } catch (JsonProcessingException e) {
//        	log.error("Serialization error");
//            throw new RuntimeException("Serialization error", e);
//        }
//        
//        try {
//        	 streamBridge.send("paymentCreate-out-0",messageJson);
//        	 log.info("FarmSetService. Send message to topic paymentCreate");
//        } catch (Exception e) {
//        	log.error("Kafka sending message error ");
//            throw new RuntimeException("Kafka sending message error", e);
//        }
//		
		long start = System.nanoTime();
        try {
            String messageJson = objectMapper.writeValueAsString(paymentRequestMessage);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("payment-create", messageJson);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent to payment-create, offset: {}, time: {} ms",
                        result.getRecordMetadata().offset(),
                        (System.nanoTime() - start) / 1_000_000);
                } else {
                    log.error("Kafka sending error for payment-create: {}, time: {} ms",
                        ex.getMessage(), (System.nanoTime() - start) / 1_000_000);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Serialization error for payment-create: {}", e.getMessage(), e);
            throw new RuntimeException("Serialization error", e);
        }
	}

	@Override
	@Transactional
	public void cancelReserve(OrderFailedMessage message) {
		
		log.info("FarmSetService. CancelReserve for farmset {}, order {} - {}", message.getFarmsetId(), message.getOrderId());
		
		
		UUID farmsetId = UUID.fromString(message.getFarmsetId());
		log.info("FarmSetService: famsetId from message - {}", message);

		FarmSet farmSet = farmSetRepo.findById(farmsetId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm Set is not found"));
			
		log.info("FarmSetService. AvailibleCount before cancel reserve - {}", farmSet.getAvailibleCount());
		farmSet.setAvailibleCount(farmSet.getAvailibleCount() +1);
		log.info("FarmSetService. AvailibleCount after cancel reserve - {}", farmSet.getAvailibleCount());
		farmSetRepo.save(farmSet);
		
	}


}
