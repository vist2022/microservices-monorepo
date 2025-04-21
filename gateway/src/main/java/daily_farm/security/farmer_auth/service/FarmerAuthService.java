package daily_farm.security.farmer_auth.service;

import static daily_farm.security.api.messages.ErrorMessages.*;
import static daily_farm.security.api.AuthApiConstants.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.email_sender.service.MailSenderService;
import daily_farm.email_sender.service.SendGridEmailSender;
import daily_farm.security.entity.Farmer;
import daily_farm.repo.FarmerCredentialRepository;
import daily_farm.farmer.service.FarmerService;
import daily_farm.security.api.dto.*;
import daily_farm.security.entity.FarmerCredential;
import daily_farm.security.token.JwtService;
import daily_farm.security.token.TokenBlacklistService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerAuthService implements IFarmerAuth{
	private final FarmerService farmerService;
	private final FarmerCredentialRepository credentialRepo;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	
	private final StringRedisTemplate redisTemplate;
	private final TokenBlacklistService blackListService;
	
	
//	????
	private final SendGridEmailSender gridSender;
	private final MailSenderService emailService;
	
	 @Value("${jwt.refresh.token.validity}")
	 private long languageCacheValidity ;
	

	
	
	public ResponseEntity<RefreshTokenResponseDto> refreshAccessTokenFarmer(String refreshToken) {

		log.info("AuthService refreshAccessToken.");

		UUID id = UUID.fromString(jwtService.extractUserId(refreshToken));
		FarmerCredential credential = credentialRepo.findById(id).orElseThrow(()->
						new BadCredentialsException(INVALID_TOKEN));
		log.info("AuthService refreshAccessToken. Checking data from refreshToken : farmer exists");
		log.info("AuthService refreshAccessToken. isTokenExpired - {}", jwtService.isTokenExpired(refreshToken));
		if (!credential.getRefreshToken().isBlank()
				&& credential.getRefreshToken().equals(refreshToken) && !jwtService.isTokenExpired(refreshToken)) {
			return ResponseEntity.ok(new RefreshTokenResponseDto(
					jwtService.generateAccessToken(id.toString(), credential.getEmail(), jwtService.extractUserRole(refreshToken))));
		} 
		throw new BadCredentialsException(INVALID_TOKEN);
	}


	@Override
	@Transactional
	public ResponseEntity<String> registerFarmer(FarmerRegistrationDto farmerDto , String lang) {
		log.info("FarmerAuthServise. Registration of new farmer - {}", farmerDto.getEmail());
		String email = farmerDto.getEmail();
		checkEmailIsUnique(email);
		log.info("FarmerAuthServise.Email is unique");
		
		Farmer farmer = Farmer.of(farmerDto);
		log.debug("FarmerAuthServise. Created Entity farmer from dto");

		FarmerCredential credential = FarmerCredential.builder().createdAt(LocalDateTime.now())
				.password_last_updated(LocalDateTime.now())
				.hashedPassword(passwordEncoder.encode(farmerDto.getPassword()))
				.farmer(farmer)
				.email(email)
				.build();
		
		log.debug("FarmerAuthServise. Created Entity farmer from dto");
		
		farmer.setCredential(credential);
		
		
		/// Rest?? Kafka??
		farmerService.createFarmer(farmer, farmerDto.getCoordinates(), lang);
		
		redisTemplate.opsForValue().set("userID-" + farmer.getId(), lang , languageCacheValidity, TimeUnit.MILLISECONDS);
	
		gridSender.sendEmailVerification(email,
				jwtService.generateVerificationToken(farmer.getId().toString(), email), FARMER_EMAIL_VERIFICATION);

		return ResponseEntity.ok("Farmer added successfully. You need to verify your email");
	}
	
	@Override
	public ResponseEntity<TokensResponseDto> loginFarmer(LoginRequestDto loginRequestDto) {
		String email = loginRequestDto.getEmail();
		log.debug("FarmerAuthService. Request to login farmer -" + email);

		FarmerCredential credential = credentialRepo.findByEmail(email).orElseThrow(()->
			new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
		
		if(!credential.isVerificated()) {
			log.debug("Service. Login. Email is not verificated. Send link to email -" + email);
			gridSender.sendEmailVerification(email,
					jwtService.generateVerificationToken(credential.getId().toString(), email), FARMER_EMAIL_VERIFICATION);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, EMAIL_IS_NOT_VERIFICATED);
		}
		
		TokensResponseDto tokens = authenticateFarmer(loginRequestDto.getEmail(), loginRequestDto.getPassword());
		log.debug("Service. Login successfull, token returned to user");
		return ResponseEntity.ok(tokens);
	}


	@Override
	@Transactional
	public ResponseEntity<String> emailVerification(String verificationToken) {
		log.info("Service.emailVerification. Request to email verification");
		try {
			
			String emailFromToken = jwtService.extractUserEmail(verificationToken);
			log.info("Service.emailVerification. Email from token - " + emailFromToken);
			if (jwtService.isTokenValid(verificationToken, emailFromToken)
					&& !jwtService.isTokenExpired(verificationToken) && !blackListService.isBlacklisted(verificationToken)) {
				log.info("Service.emailVerification. Token is valid - " + emailFromToken);
				FarmerCredential credential = credentialRepo.findByEmail(emailFromToken).orElseThrow(
						() -> new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
				log.info("Service.emailVerification Farmer exists");
				

				credential.setVerificated(true);
				blackListService.addToBlacklist(verificationToken);
				log.info("Service.emailVerification Set  verificated true - " + emailFromToken);
			}else
				throw new JwtException(INVALID_TOKEN);
		} catch (Exception e) {
			log.error("Service.emailVerification Invalid token" + INVALID_TOKEN);
			throw new JwtException(INVALID_TOKEN);
		}
		return ResponseEntity.ok(EMAIL_IS_VERIFICATED);
	}


	@Override
	public ResponseEntity<String> resendVerificationLink(String email) {
		FarmerCredential credential = credentialRepo.findByEmail(email).orElseThrow(()->
			new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));

		emailService.sendEmailVerification(email, jwtService.generateVerificationToken(credential.getId().toString(),email), FARMER_EMAIL_VERIFICATION );
		return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK);
	}

	@Override
	@Transactional
	public ResponseEntity<String> removeFarmer(UUID id) {
		if (!credentialRepo.existsById(id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS);
		log.info("user exists");
		credentialRepo.deleteById(id);
		return ResponseEntity.ok("Farmer removed");
	}


	

	@Override
	@Transactional
	public ResponseEntity<String> logoutFarmer(UUID id, String token) {
		log.info("Service.logoutFarmer Logout starts");
		FarmerCredential credential = credentialRepo.findById(id).get();
		token = token.substring(7);
		blackListService.addToBlacklist(token);
		log.info("Service.logoutFarmer AccessToken added to black list");
		credential.setRefreshToken("");
		log.info("Service.logoutFarmer RefreshToken removed from credential");
		return ResponseEntity.ok("Farmer removed");
	}


	@Override
	@Transactional
	public ResponseEntity<TokensResponseDto> updatePassword(UUID id, ChangePasswordRequest changePasswordDto) {
	
		FarmerCredential credential = credentialRepo.findById(id).get();
		String oldPassword = credential.getHashedPassword();
		log.info("Service. updatePassword. Old password is - " + oldPassword);
		if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), oldPassword)) {
			throw new IllegalArgumentException("Invalid old password");
		}
		log.info("Service. updatePassword. Old password - " + credential.getHashedPassword());
		credential.setHashedPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
		log.info("Service. updatePassword. New password - " + credential.getHashedPassword());
		credential.setPassword_last_updated(LocalDateTime.now());

		TokensResponseDto tokens = authenticateFarmer(credential.getEmail(), changePasswordDto.getNewPassword() );

		return ResponseEntity.ok(tokens);
	}


	@Override
	@Transactional
	public ResponseEntity<String> generateAndSendNewPassword(String email) {
		FarmerCredential credential = credentialRepo.findByEmail(email).orElseThrow(()->
			new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
		log.info("Service. getResetPassword(). Farmer exists");
		
		
		String genPassword = jwtService.generatePassword(10);
		credential.setHashedPassword(passwordEncoder.encode(genPassword));
		
		log.info("Service. Password hashed and saved to credential");
		credential.setPassword_last_updated(LocalDateTime.now());
		
		gridSender.sendResetPassword(email, genPassword);
		log.info("Service. Password was send to email ");
		
	return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK);
	}
	
	
	private void checkEmailIsUnique(String email) {
		if (credentialRepo.existsByEmail(email))
			throw new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_EXISTS);
	}


	@Override
	public ResponseEntity<String> sendVerificationTokenForUpdateEmail(UUID id, String newEmail) { // todo validation for email
		Optional<FarmerCredential> optCredintial = credentialRepo.findByEmail(newEmail);
		if(optCredintial.isPresent())
			throw new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_EXISTS);
		
		FarmerCredential credential = credentialRepo.findById(id).get();
		String email = credential.getEmail();
		gridSender.sendChangeEmailVerification(email, jwtService.generateVerificationTokenForUpdateEmail(credential.getId().toString(),email, newEmail));
		return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK + " - " + email);
	}


	@Override
	public ResponseEntity<String> sendVerificationTokenToNewEmail(String verificationToken) {
		
		String newEmailFromToken = jwtService.extractUserNewEmail(verificationToken);
		String oldEmailFromToken = jwtService.extractUserEmail(verificationToken);
		
		if (jwtService.isTokenValid(verificationToken, oldEmailFromToken)
				 && !blackListService.isBlacklisted(verificationToken)) {
			String id = jwtService.extractUserId(verificationToken);
			String newToken = jwtService.generateVerificationTokenForUpdateEmail(id, oldEmailFromToken, newEmailFromToken);
			gridSender.sendVerificationTokenToNewEmail(newEmailFromToken, newToken);
			blackListService.addToBlacklist(verificationToken);
		}else
			throw new JwtException(INVALID_TOKEN);
		return ResponseEntity.ok(CHECK_EMAIL_FOR_VERIFICATION_LINK + " - " + newEmailFromToken);
	}


	@Override
	@Transactional
	public ResponseEntity<String> updateEmail(String verificationToken) { 
		
		
		log.info("Service. Request updateEmail");
		try {
			String oldEmailFromToken = jwtService.extractUserEmail(verificationToken);
			String newEmailFromToken = jwtService.extractUserNewEmail(verificationToken);
			log.info("Service.updateEmail Email from token - " + oldEmailFromToken);
			log.info("Service.updateEmail New email from token - " + newEmailFromToken);
			if (jwtService.isTokenValid(verificationToken, oldEmailFromToken)
					 && !blackListService.isBlacklisted(verificationToken)) {
				
				
				FarmerCredential credential = credentialRepo.findByEmail(oldEmailFromToken).orElseThrow(
						() -> new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_IS_NOT_EXISTS));
				Optional<FarmerCredential> optFarmer =credentialRepo.findByEmail(oldEmailFromToken);
				if(optFarmer.isPresent())
					throw new ResponseStatusException(HttpStatus.CONFLICT, FARMER_WITH_THIS_EMAIL_EXISTS);
				log.info("Service.updateEmail Farmer exists");
				credential.setEmail(newEmailFromToken);
				log.info("Service.updateEmail New email saved - " + newEmailFromToken);
				blackListService.addToBlacklist(verificationToken);
				log.info("Service.updateEmail. Token added to blacklist");
			}else
				throw new JwtException(INVALID_TOKEN);
		} catch (Exception e) {
			log.error("Service. invalid token" + INVALID_TOKEN);
			throw new JwtException(INVALID_TOKEN);
		}
		return ResponseEntity.ok(EMAIL_IS_VERIFICATED);
	}
	
	
	
	private TokensResponseDto authenticateFarmer(String email, String password) {

		Optional<FarmerCredential> credentialOptional = credentialRepo.findByEmail(email);

		if (credentialOptional.isPresent()) {
			FarmerCredential credential = credentialOptional.get();
			log.info("Authenticate. Farmer {} exists ", credential.getEmail());

			log.info("Authenticate. passwordEncoder.matches - {}",
					 passwordEncoder.matches(password, credential.getHashedPassword()));
			if (passwordEncoder.matches(password, credential.getHashedPassword())) {
				log.info("Authenticate. Password is valid");
				String uuid = credential.getId().toString();

				String accessToken = jwtService.generateAccessToken(uuid, email, "farmer");
				log.info("access token - {}", accessToken);
				String refreshToken = jwtService.generateRefreshToken(uuid, email, "farmer");
				log.info("refresh token - {}", refreshToken);

				credential.setRefreshToken(refreshToken);
				credentialRepo.save(credential);
				
				log.info("login success!!! ");
				return new TokensResponseDto(accessToken, refreshToken);
			}
		}
		throw new BadCredentialsException(WRONG_USER_NAME_OR_PASSWORD);
	}

}
