package daily_farm.auth.customer_auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static daily_farm.auth.api.messages.ErrorMessages.*;

//import daily_farm.auth.customer_auth.entity.Customer;
//import daily_farm.customer.entity.CustomerCredential;
//import daily_farm.customer.repo.CustomerCredentialRepository;
//import daily_farm.customer.repo.CustomerRepository;
//import daily_farm.security.UserDetailsWithId;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDetailsService implements UserDetailsService {
//	private final CustomerRepository customerRepo;
//	private final CustomerCredentialRepository customerCredentialRepo;
//
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Optional<Customer> customerOptional = customerRepo.findByEmail(username);
//		if (customerOptional.isPresent()) {
//			Customer customer = customerOptional.get();
//			CustomerCredential customerCredential = customerCredentialRepo.findByCustomer(customer);
//			return new UserDetailsWithId(customer.getEmail(), customerCredential.getHashedPassword(),
//					List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")), customer.getId());
//		}
//
//		throw new UsernameNotFoundException(USER_NOT_FOUND);
		return null;
	}
}