package daily_farm.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import daily_farm.entity.FarmSet;
import jakarta.persistence.LockModeType;

public interface FarmSetRepository extends JpaRepository<FarmSet, UUID>{

	List<FarmSet> findByFarmerId(UUID id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT f FROM FarmSet f WHERE f.id = :id")
	Optional<FarmSet> findByIdForUpdate(@Param("id") UUID id);


	@Modifying
	@Query("UPDATE FarmSet f SET f.availibleCount = f.availibleCount - 1 WHERE f.id = :id AND f.availibleCount > 0")
	int decreaseIfAvailable(@Param("id") UUID id);

}
