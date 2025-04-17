package telran.daily_farm.order.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import telran.daily_farm.order.api.dto.OrderStatus;
import telran.daily_farm.payment.entity.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "farm_set_orders")
public class OrderFarmSet {
	@Id
	@UuidGenerator
	@GeneratedValue
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

	@OneToOne(mappedBy = "orderFarmSet", cascade = CascadeType.ALL)
	private Payment payment;
	
	public OrderFarmSet(UUID id) {
		this.id = id;
	}
	
}
