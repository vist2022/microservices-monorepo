package daily_farm.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import daily_farm.api.dto.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
    @Column(nullable = false)
    private UUID orderId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    
    @Column(nullable = true)
    private String paymentLink;
}
