package daily_farm.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.security.entity.Farmer;
import daily_farm.security.entity.FarmerCredential;

public interface FarmerCredentialRepository extends JpaRepository<FarmerCredential, UUID> {

	FarmerCredential findByFarmer(Farmer farmer);

	Optional<FarmerCredential> findByEmail(String email);

	boolean existsByEmail(String email);

}
