package daily_farm.order.service;

import java.util.UUID;

import daily_farm.order.entity.OrderFarmSet;
import daily_farm.order.api.dto.CreateOrderResponseDto;
import daily_farm.order.api.dto.OrderRequestMessage;

public interface IOrderService {

	//String createOrder(UUID farmsetId, UUID customerId);
	
	void orderCreate(OrderRequestMessage message); 

	void checkPendingPayments();

	void cancelOrder(OrderFarmSet order);
}
