package telran.daily_farm.order.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.daily_farm.order.entity.OrderFarmSet;

public interface OrderRepository extends JpaRepository<OrderFarmSet, UUID>{

	@Query(value = "SELECT * FROM farm_set_orders WHERE status = 'WAITING_FOR_PAYMENT' AND created_at < :threshold", nativeQuery = true)
    List<OrderFarmSet> findExpiredOrders(@Param("threshold") LocalDateTime threshold);
}
