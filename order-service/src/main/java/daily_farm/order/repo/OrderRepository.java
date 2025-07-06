package daily_farm.order.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import daily_farm.order.api.dto.OrderStatus;
import daily_farm.order.entity.OrderFarmSet;

public interface OrderRepository extends JpaRepository<OrderFarmSet, UUID>{

	@Query(value = "SELECT * FROM farm_set_orders WHERE status = 'WAITING_FOR_PAYMENT' AND created_at < :threshold", nativeQuery = true)
    List<OrderFarmSet> findExpiredOrders(@Param("threshold") LocalDateTime threshold, PageRequest pageRequest);

//	boolean findByIdAndOrderStatus(UUID orderId, OrderStatus waitingForPayment);
	
	@Modifying
	@Query("UPDATE OrderFarmSet order SET order.status = :status WHERE order.id = :id")
	void updateStatusIfAvailable(@Param("id") UUID id, @Param("status") OrderStatus status) ;
}
