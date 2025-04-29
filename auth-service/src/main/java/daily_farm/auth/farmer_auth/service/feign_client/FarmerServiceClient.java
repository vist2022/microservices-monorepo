package daily_farm.auth.farmer_auth.service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import daily_farm.auth.api.dto.farmer_service.FarmerRegistrationRequestDto;



@FeignClient(name = "customerService", url = "http://localhost:8083")
public interface FarmerServiceClient {

	@PostMapping("/farmer-data")
    ResponseEntity<String> createFamrerData(@RequestBody FarmerRegistrationRequestDto dto); 
}
