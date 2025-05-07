package daily_farm.order.service;

import java.util.UUID;

import daily_farm.order.entity.OrderFarmSet;
import daily_farm.order.api.dto.CreateOrderResponseDto;

public interface IOrderService {

	CreateOrderResponseDto createOrder(UUID farmsetId, UUID customerId);

	void checkPendingPayments();

	void cancelOrder(OrderFarmSet order);
}
