package daily_farm.security.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import daily_farm.security.api.dto.FarmerRegistrationDto;
import daily_farm.security.entity.FarmerCredential;
import daily_farm.security.api.dto.CoordinatesDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Entity
@ToString
@Table(name = "farmers")
public class Farmer {

	@Id
	@GeneratedValue
	@UuidGenerator
	UUID id;

//	@Column(unique = true, nullable = false)
//	String email;

	@Column(nullable = false)
	String company;

	@Column(nullable = false)
	String phone;

	@Column(nullable = false)
	String farmerLanguage;

	Double balance;

	@OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
	Coordinates coordinates;


	@OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
	FarmerCredential credential;

//	@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
//	List<FarmSet> farmSets;

//	@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
//	@Column
//	List<UUID> orders;

//	@Embedded
//	PayPalConfigDto paypalDetails;

//	@OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
//	Address address;

	public Farmer(UUID id) {
		this.id = id;
	}

	public static Farmer of(FarmerRegistrationDto dto) {
		return Farmer.builder()
//				.email(dto.getEmail())
				.phone(dto.getPhone()).company(dto.getCompany()).build();
	}

}
