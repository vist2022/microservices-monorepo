package daily_farm.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.entity.FarmSetCategory;

public interface FarmSetCategoryRepository extends JpaRepository<FarmSetCategory, UUID>{

	Optional<FarmSetCategory> findByCategory(String category);

}
