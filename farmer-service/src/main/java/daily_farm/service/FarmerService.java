package daily_farm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;
import daily_farm.entity.Farmer;
import daily_farm.repo.FarmerRepository;
//import daily_farm.location.service.ILocationService;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FarmerService implements IFarmer {

	private final FarmerRepository farmerRepo;
	private final StringRedisTemplate redisTemplate;
	//private final AddressRepository addressRepo;


//	@Autowired
//	ILocationService locationService;
	
	 @Value("${language.cache.validity}")
	 private long languageCacheValidity ;


	@Override
	@Transactional
	public ResponseEntity<String> updateFarmer(UUID id, FarmerUpdateDataRequestDto farmerDto) {
		log.info("Service. Update farmer data starts");

		Farmer farmer = farmerRepo.findByid(id).get();
		farmer.setPhone(farmerDto.getPhone());
		log.info("Service. Phone updated");
		farmer.setCompany(farmerDto.getCompany());
		log.info("Service. Phone updated");
		farmer.setCoordinates(farmerDto.getCoordinates());
		log.info("coordinates updated successfully");

		return ResponseEntity.ok("Data updated successfuly");
	}

//	@Override
//	@Transactional
//	public ResponseEntity<String> updateAddress(UUID id, AddressDto addressDto) {
//
//		addressRepo.findByFarmer(new Farmer(id)).updateFromDto(addressDto);
//		coordinatesRepo.findByFarmer(new Farmer(id))
//				.updateFromDto(locationService.getCoordinatesFromAddress(addressDto));
//
//		return ResponseEntity.ok("Coordinates and address updated successfully");
//	}

	@Override
	@Transactional
	public ResponseEntity<String> updateCoordinates(UUID id, CoordinatesDto coordinatesDto) {
		farmerRepo.findById(id).get().setCoordinates(coordinatesDto);
//		addressRepo.findByFarmer(new Farmer(id))
//				.updateFromDto(locationService.getAddtessFromCoordinates(coordinatesDto));
		return ResponseEntity.ok("Coordinates and address updated successfully");
	}
	

	@Override
	@Transactional
	public ResponseEntity<String> updatePhone(UUID id, String newPhone) {
		farmerRepo.findByid(id).get().setPhone(newPhone);
		return ResponseEntity.ok("Phone updated successfully");

	}

	
	@Override
	@Transactional
	public ResponseEntity<String> updateCompany(UUID id, String company) {
		farmerRepo.findByid(id).get().setCompany(company);
		return ResponseEntity.ok("Company updated successfully");
	}
	
	@Transactional
	@Override
	public ResponseEntity<String> createFarmerData(FarmerRegistrationRequestDto dto) {
		
		Farmer farmer = Farmer.of(dto);
		farmerRepo.save(farmer);
		log.info("FarmerServise. Farmer-data saved to database");
		return ResponseEntity.ok().build();
	}
	
	@Override
	@Transactional
	public ResponseEntity<Void> changeLanguage(UUID id, String language) {
		
		farmerRepo.findByid(id).get().setLanguage(language);
		redisTemplate.opsForValue().set("userID-" + id, language , languageCacheValidity, TimeUnit.MILLISECONDS);
		
		return ResponseEntity.ok().build();
	}

	@Override
	public Farmer getFarmer(UUID id) {
		
		return farmerRepo.findByid(id).get();
	}
}
