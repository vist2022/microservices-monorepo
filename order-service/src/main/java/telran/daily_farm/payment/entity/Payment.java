package telran.daily_farm.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import telran.daily_farm.order.entity.OrderFarmSet;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue
	@UuidGenerator
	UUID id; 
	
	@Column(nullable = false)
    private String provider;
    
	@Column(nullable = false)
    private String paymentProviderId;
	
	@Column(nullable = false)
    private String status; 
	
	@Column(nullable = false)
    private double amount; 
	
	@Column(nullable = false)
    private boolean refunded;
	
	@Column(nullable = false)
    private LocalDateTime createdAt; 
    
    private LocalDateTime updatedAt ;
	
    @OneToOne
    @JoinColumn(name = "order_farm_set_id", referencedColumnName = "id")
    private OrderFarmSet orderFarmSet;
}
