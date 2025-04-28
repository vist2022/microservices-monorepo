package daily_farm.auth.customer_auth.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import daily_farm.auth.customer_auth.entity.*;

public interface CustomerCredentialRepository extends JpaRepository<CustomerCredential, UUID> {

   

	Optional<CustomerCredential> findByEmail(String email);

	boolean existsByEmail(String email);
}
