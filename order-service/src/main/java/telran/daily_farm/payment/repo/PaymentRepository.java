package telran.daily_farm.payment.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.daily_farm.order.entity.OrderFarmSet;
import telran.daily_farm.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID>{

	Optional<Payment> findByPaymentProviderId(String orderId);

	
	Payment findPaymentByPaymentProviderId(String paymentProviderId);


	Optional<Payment> findByOrderFarmSet(OrderFarmSet orderFarmSet);

}
