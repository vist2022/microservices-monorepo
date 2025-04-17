package telran.daily_farm.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.daily_farm.order.api.dto.FarmSetRequestForOrderDto;
import telran.daily_farm.order.api.dto.CreateOrderResponseDto;
import telran.daily_farm.order.api.dto.FarmSetRequestForCancelOrderDto;
import telran.daily_farm.order.api.dto.FarmSetResponseForOrderDto;
import telran.daily_farm.order.api.dto.OrderStatus;
import telran.daily_farm.order.entity.OrderFarmSet;
import telran.daily_farm.order.repo.OrderRepository;
import telran.daily_farm.payment.service.PaymentService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

	private final FarmSetFeignClient farmSetClient;
	private final OrderRepository orderRepo;
	private final PaymentService paymentService;

	@Override
	@Transactional
	public CreateOrderResponseDto createOrder(UUID farmsetId, UUID customerId) {
		log.info("OrderService: Create order request");

		FarmSetResponseForOrderDto farmSetResponse = null;
		try {
			log.info("createOrder. farmsetId - {}", farmsetId);
			farmSetResponse = farmSetClient.decreaseStock(new FarmSetRequestForOrderDto(farmsetId));
			log.info("OrderService : Create order. Recieved farmset ID from database - {}",
					farmSetResponse.getFarmSetId());
			log.info("OrderService : Create order. Recieved farmer ID from database  - {}",
					farmSetResponse.getFarmerId());

		} catch (FeignException e) {

			throw new RuntimeException("Failed to decrease stock: " + e.getMessage());
		}

		OrderFarmSet orderFarmSet = OrderFarmSet.builder().farmSetId(farmsetId).customerId(customerId)
				.farmerId(farmSetResponse.getFarmerId()).status(OrderStatus.WAITING_FOR_PAYMENT)
				.createdAt(LocalDateTime.now()).sumOfOrder(farmSetResponse.getPrice()).build();
		log.info("OrderService : Create order - Created new order");

		orderRepo.save(orderFarmSet);
		log.info("OrderService : Create order - new order saved to database ");

		String approvalLink = paymentService.createPayment(orderFarmSet.getId(), orderFarmSet.getSumOfOrder(),
				"PayPal");
		log.info("OrderService : Create order - link for payment generated - {} ", approvalLink);

		CreateOrderResponseDto response = new CreateOrderResponseDto(orderFarmSet.getSumOfOrder(), "message",
				approvalLink);
		log.info("OrderService : Create order - Response for customer created!");

		return response;
	}

	@Override
	@Scheduled(fixedRate = 60000)
	@Transactional
	public void checkPendingPayments() {
		LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(1);
		List<OrderFarmSet> expiredOrders = orderRepo.findExpiredOrders(tenMinutesAgo);
		log.info("OrderService : Checking payment....", expiredOrders.size());
		for (OrderFarmSet order : expiredOrders) {
			log.info("Cancel order {} — not paid", order.getId());
			cancelOrder(order);

		}
	}

	@Override
	@Transactional
	public void cancelOrder(OrderFarmSet order) {
		log.info("cancelOrder: orderid - {}", order.getId());
		log.info("cancelOrder: farmsetId - {}", order.getFarmSetId());
		try {
			farmSetClient.increaseStock(new FarmSetRequestForCancelOrderDto(order.getFarmSetId()));
		} catch (FeignException.BadRequest e) {
			log.warn("Failed to increase stock due to bad request: {}", e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel order: " + e.getMessage(), e);
		}  catch (FeignException.NotFound e) {
			log.warn("FarmSet not found: {}", e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FarmSet not found", e);
		} catch (FeignException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to increase stock: " + e.getMessage());
		}

		log.info("OrderService : Cancel order - add count to farmset");
		// paymentService.cancelPaymentOrder(order);
		order.setStatus(OrderStatus.CANCELLED);
		log.info("OrderService : Cancel order - changing status for order to CANCEL");
	}
}
