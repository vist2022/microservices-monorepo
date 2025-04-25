package daily_farm.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import  daily_farm.entity.Customer;
import daily_farm.entity.CustomerCredential;

public interface CustomerCredentialRepository extends JpaRepository<CustomerCredential, UUID> {

    CustomerCredential findByCustomer(Customer customer);
}
