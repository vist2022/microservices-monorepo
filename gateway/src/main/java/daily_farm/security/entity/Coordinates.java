package daily_farm.security.entity;


import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

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
import daily_farm.security.api.dto.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "farmers_coordinates")
public class Coordinates {

	@Id
	@GeneratedValue
	@UuidGenerator
	UUID id;
	
	@OneToOne
	@JoinColumn(name = "farmer_id", nullable = false)
	Farmer farmer;
	
	private Double latitude;
	private Double longitude;
	
	public static Coordinates of(CoordinatesDto dto) {
		return Coordinates.builder().latitude(dto.getLatitude()).longitude(dto.getLongitude()).build();
	}
	public void updateFromDto(CoordinatesDto dto) {
	    this.latitude = dto.getLatitude();
	    this.longitude = dto.getLongitude();
	}
}
