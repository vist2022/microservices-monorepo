package daily_farm.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.security.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, UUID>{

	

	

	Optional<Farmer> findByid(UUID id);



}
