package daily_farm.auth.farmer_auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Builder
@Setter
@Getter
@Table(name = "farmer_credentials")
public class FarmerCredential {
   
	
	@Id
	@GeneratedValue
    private UUID farmerId;

   
	@Column(unique = true, nullable = false)
	private String email;


    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private boolean isVerificated;
    
    @Column(nullable = false)
    private LocalDateTime password_last_updated;
    
    private String refreshToken;
}
