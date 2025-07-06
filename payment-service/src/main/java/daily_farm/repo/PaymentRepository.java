package daily_farm.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import daily_farm.api.dto.OrderStatus;

import daily_farm.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID>{

	Optional<Payment> findByPaymentProviderId(String orderId);

	
	Payment findPaymentByPaymentProviderId(String paymentProviderId);


	//Optional<Payment> findByOrderFarmSet(OrderFarmSet orderFarmSet);


	Optional<Payment> findByOrderId(UUID orderId);
	
	@Modifying
	@Query("UPDATE Payment p SET p.orderStatus = :status WHERE p.id = :id")
	int updateStatusIfAvailable(@Param("id") UUID id, @Param("status") OrderStatus status) ;


	

}
