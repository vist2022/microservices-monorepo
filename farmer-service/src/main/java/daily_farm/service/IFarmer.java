package daily_farm.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import daily_farm.api.dto.FarmerRegistrationRequestDto;
import daily_farm.api.dto.CoordinatesDto;
import daily_farm.api.dto.FarmerUpdateDataRequestDto;
import daily_farm.entity.Farmer;

public interface IFarmer {

	ResponseEntity<String> createFarmerData(FarmerRegistrationRequestDto dto);


	ResponseEntity<String> updateCoordinates(UUID uuid, CoordinatesDto coordinatesDto);

	ResponseEntity<String> updatePhone(UUID uuid, String newPhone);

	ResponseEntity<String> updateFarmer(UUID id, FarmerUpdateDataRequestDto farmerDto);

	ResponseEntity<String> updateCompany(UUID id, String company);
	
	ResponseEntity<Void> changeLanguage(UUID id, String language);
	
	Farmer getFarmer(UUID id);

}
