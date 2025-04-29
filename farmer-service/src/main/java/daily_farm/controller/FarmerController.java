package daily_farm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;
import daily_farm.service.IFarmer;

import static daily_farm.api.messages.ErrorMessages.*;

import java.util.UUID;

import static daily_farm.api.FarmerApiConstants.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FarmerController {

	
	private final IFarmer farmerService;
	
	
	@PostMapping(FARMER_EDIT)
	public ResponseEntity<String> createFarmerData(@Valid @RequestBody FarmerRegistrationRequestDto farmerDto){
			
		
            return farmerService.createFarmerData(farmerDto);
     

	}

	@PatchMapping(FARMER_EDIT)
	public ResponseEntity<String> updateFarmer(@Valid @RequestBody FarmerUpdateDataRequestDto farmerDto,
			@RequestHeader(value = "X-User-Id", required = true) String id,
			 @RequestHeader("Authorization") String token) {
		try {
            UUID userId = UUID.fromString(id);
            return farmerService.updateFarmer(userId, farmerDto);
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
		

	}

	@PutMapping(FARMER_CHANGE_COORDINATES)
	public ResponseEntity<String> farmerUpdateCoordinates(@Valid @RequestBody CoordinatesDto coordinatesDto,
			@RequestHeader(value = "X-Latitude", required = false) Double latitude,
			@RequestHeader(value = "X-Longitude", required = false) Double longitude,
			@RequestHeader(value = "X-User-Id", required = true) String id,
			@RequestHeader("Authorization") String token) {

		if ((latitude == null && longitude == null) && coordinatesDto == null)
			return ResponseEntity.badRequest().body(LOCATION_REQUIRED);
		if (coordinatesDto == null)
			coordinatesDto = new CoordinatesDto(latitude, longitude);

		try {
            UUID userId = UUID.fromString(id);
            return farmerService.updateCoordinates(userId, coordinatesDto);
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
	}

	@PatchMapping(FARMER_CHANGE_PHONE)
	public ResponseEntity<String> farmerUpdatePhone(@Valid @RequestBody UpdatePhoneRequestDto updatePhoneDto,
			@RequestHeader(value = "X-User-Id", required = true) String id,
		 @RequestHeader("Authorization") String token) {
		
		try {
            UUID userId = UUID.fromString(id);
            return  farmerService.updatePhone(userId, updatePhoneDto.getPhone());
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
	}

	@PatchMapping(FARMER_CHANGE_COMPANY_NAME)
	public ResponseEntity<String> farmerUpdateCompany(@Valid @RequestBody UpdateCompanyRequestDto updateCompanyDto,
			@RequestHeader(value = "X-User-Id", required = true) String id,
			 @RequestHeader("Authorization") String token) {
		
		try {
            UUID userId = UUID.fromString(id);
            return  farmerService.updateCompany(userId, updateCompanyDto.getCompany());
        } catch (IllegalArgumentException e) {
            log.error("Invalid X-User-Id format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid X-User-Id format");
        }
	}
	

	@PostMapping(CHANGE_USER_LANGUAGE)
	public ResponseEntity<Void> changeLanguage( @RequestHeader("Authorization") String token,
			@RequestHeader(value = "X-User-Id", required = true) String id,
			@Valid @RequestBody UpdateLanguageRequestDto updateLanguageDto) {
		log.info("changeLanguage to - {}",updateLanguageDto.getNewLanguage() );
		
		
		//todo
		
		
//		if(!translateService.getAllLanguages().keySet().contains(updateLanguageDto.getNewLanguage()))
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application does not support current language");
//			
		
		
		
		 UUID userId = UUID.fromString(id);
		
		return farmerService.changeLanguage(userId, updateLanguageDto.getNewLanguage());
	}



}
