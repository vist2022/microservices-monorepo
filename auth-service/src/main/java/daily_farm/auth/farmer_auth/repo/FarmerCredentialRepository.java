package daily_farm.auth.farmer_auth.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import daily_farm.auth.farmer_auth.entity.FarmerCredential;

public interface FarmerCredentialRepository extends JpaRepository<FarmerCredential, UUID> {

	//FarmerCredential findByFarmer(Farmer farmer);

	Optional<FarmerCredential> findByEmail(String email);

	boolean existsByEmail(String email);

}
