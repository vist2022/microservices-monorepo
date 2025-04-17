package telran.daily_farm.order.service;

import java.util.UUID;

import telran.daily_farm.order.api.dto.FarmSetRequestForOrderDto;
import telran.daily_farm.order.api.dto.CreateOrderResponseDto;
import telran.daily_farm.order.entity.OrderFarmSet;

public interface IOrderService {

	CreateOrderResponseDto createOrder(UUID farmsetId, UUID customerId);

	void checkPendingPayments();

	void cancelOrder(OrderFarmSet order);
}
