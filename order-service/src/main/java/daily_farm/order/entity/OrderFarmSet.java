package daily_farm.order.entity;

import java.time.LocalDateTime;
import java.util.UUID;


import daily_farm.order.api.dto.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "farm_set_orders",indexes = {
        @Index(name = "idx_order_created_status", columnList = "created_at, status"),
        @Index(name = "idx_order_id", columnList = "id")
    })
public class OrderFarmSet {
	@Id
//	@UuidGenerator
//	@GeneratedValue
	private UUID id;
	
	 @Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private double sumOfOrder;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;


	
	@Column(name = "farmer_id", nullable = false)
	private UUID farmerId;
	
	
	@Column(name = "customer_id", nullable = false)
	private UUID customerId;
	
	
	@Column(name = "farmset_id", nullable = false)
	private UUID farmSetId;


	public OrderFarmSet(UUID id) {
		this.id = id;
	}
	
}
