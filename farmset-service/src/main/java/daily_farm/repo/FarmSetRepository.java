package daily_farm.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.entity.FarmSet;

public interface FarmSetRepository extends JpaRepository<FarmSet, UUID>{

	List<FarmSet> findByFarmerId(UUID id);

	


}
