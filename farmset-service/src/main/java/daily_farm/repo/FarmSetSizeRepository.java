package daily_farm.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.entity.FarmSetSize;

public interface FarmSetSizeRepository  extends JpaRepository<FarmSetSize, UUID>{

	Optional<FarmSetSize> findBySize(String size);

}
