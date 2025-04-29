package daily_farm.entity;

import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import daily_farm.api.dto.*;


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
	@Column(unique = true, nullable = false)
	UUID id;

	@Column(nullable = false)
	String company;

	@Column(nullable = false)
	String phone;

	@Column(nullable = false)
	String language;

//	@OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
	@Column(nullable = false)
	@Embedded
	CoordinatesDto coordinates;



//	@Embedded
//	PayPalConfigDto paypalDetails;

//	@OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
//	Address address;

	public Farmer(UUID id) {
		this.id = id;
	}

	public static Farmer of(FarmerRegistrationRequestDto dto) {
		return Farmer.builder()
				.id(dto.getId())
				.phone(dto.getPhone())
				.company(dto.getCompany())
				.language(dto.getLanguage())
				.coordinates(dto.getCoordinates())
				.build();
	}

}
