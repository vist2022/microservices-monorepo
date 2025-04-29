package daily_farm.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import daily_farm.api.dto.FarmSetResponseDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "farm_set")
public class FarmSet {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private int availibleCount;

	@Column(nullable = false)
	private boolean abailible;

	@Column(nullable = false)
	private LocalDateTime pickupTimeStart;

	@Column(nullable = false)
	private LocalDateTime pickupTimeEnd;

//	@ManyToOne
//	@JoinColumn(name = "farmer_id", nullable = false)
	@Column(name = "farmer_id", nullable = false)
	private UUID farmerId;

	@ManyToOne
	@JoinColumn(name = "size_id", nullable = false)
	private FarmSetSize size;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private FarmSetCategory category;



	public FarmSet(UUID id) {
			this.id = id;
		}

	public static FarmSetResponseDto buildFromEntity(FarmSet fs) {
		return FarmSetResponseDto.builder().category(fs.category.category).size(fs.size.size).id(fs.id)
				.availibleCount(fs.availibleCount).description(fs.description).price(fs.price)
				.pickupTimeStart(fs.pickupTimeStart).pickupTimeEnd(fs.pickupTimeEnd)

				.build();
	}
}
