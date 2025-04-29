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
@Table(name = "farm_set_category")
public class FarmSetCategory {

	@Id
	@GeneratedValue
	@UuidGenerator
	UUID id;
	
	
	@Column(name = "category", nullable = false)
	String category;
	
	@OneToMany(mappedBy = "category")
	Set<FarmSet> farmSets; 
	
}
