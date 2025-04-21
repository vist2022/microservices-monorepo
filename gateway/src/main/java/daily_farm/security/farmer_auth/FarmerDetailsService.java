package daily_farm.security.farmer_auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static daily_farm.security.api.messages.ErrorMessages.*;

import daily_farm.repo.FarmerCredentialRepository;
import daily_farm.security.UserDetailsWithId;
import daily_farm.security.entity.FarmerCredential;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerDetailsService implements UserDetailsService {
	private final FarmerCredentialRepository credentialRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		Optional<FarmerCredential> credentialOptional = credentialRepo.findByEmail(username);
		if (credentialOptional.isPresent()) {
			log.info("FarmerDetailsService: Farmer with email {} exists", username);
			FarmerCredential credential = credentialOptional.get();
			UserDetailsWithId userDetail = new UserDetailsWithId(credential.getEmail(), credential.getHashedPassword(),
					List.of(new SimpleGrantedAuthority("ROLE_FARMER")), credential.getId());
			log.info("FarmerDetailsService: Farmer with email {} has role -  {}", username, userDetail.getAuthorities().toArray()[0]);
			return userDetail;
		}

		throw new UsernameNotFoundException(USER_NOT_FOUND);
	}
}