package daily_farm.entity;

import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "farm_set_size")

public class FarmSetSize {
	
	@Id
	@GeneratedValue
	@UuidGenerator
	UUID id;
	
	@Column(name = "size", nullable = false)
	String size;
	
	
	@Getter
	@OneToMany(mappedBy = "size")
	Set<FarmSet> farmSets;


}
