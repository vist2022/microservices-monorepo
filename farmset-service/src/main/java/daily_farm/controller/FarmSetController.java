package daily_farm.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import daily_farm.api.dto.*;
import daily_farm.service.FarmSetService;


import static daily_farm.api.FarmSetApiConstants.*;

@RestController
@AllArgsConstructor
@Slf4j
public class FarmSetController {

	private final FarmSetService farmSetService;

	@PostMapping(ADD_FARM_SET)
	public ResponseEntity<Void> addFarmSet(@Valid @RequestBody FarmSetDto farmSetDto,
		@RequestHeader("X-User-Id") String id,
		@RequestHeader("Authorization") String token) {
		log.info("FarmSetController. Request from farmer - {} to add farm-set", id);
		return farmSetService.addFarmSet(UUID.fromString(id), farmSetDto);
	}


	@GetMapping(GET_ALL_SETS_BY_FARMER)
	public ResponseEntity<List<FarmSetResponseDto>> getAbailableFarmSetsForFarmer(
			@RequestHeader("X-User-Id") String id,
		 @RequestHeader("Authorization") String token) {
		return farmSetService.getAbailableFarmSetsForFarmer(UUID.fromString(id));
	}

	
	@GetMapping(GET_ALL_SETS)
	public ResponseEntity<List<FarmSetResponseDto>> getAllFarmSets() {
		return farmSetService.getAllFarmSets();
	}
	
//	@PutMapping(FARM_SET_DECREASE_STOK_FOR_ORDER)
//	public ResponseEntity<FarmSetResponseForOrderDto> decreaseStock(@Valid @RequestBody FarmSetRequestForOrderDto farmSetRequestDto) {
//	log.info("FarmSetController: start decreaseStock. Recieved FarmSetRequestForOrderDto - {}", farmSetRequestDto.getFarmSetId());
//		return farmSetService.decreaseStock(farmSetRequestDto);
//	}
	
	@GetMapping(GET_FARM_SET_FOR_ORDER)
	public ResponseEntity<FarmSetResponseForOrderDto> getFarmSet(@RequestParam  UUID farmSetId) {
	log.info("FarmSetController: getFarmSet - {}", farmSetId);
		return farmSetService.getFarmSet(farmSetId);
	}
	
	@PutMapping(FARM_SET_INCREASE_STOK_FOR_ORDER)
	public ResponseEntity<Void> increaseStock(@Valid @RequestBody FarmSetRequestForCancelOrderDto farmSetRequestDto) {
		log.info("FarmSetController: start increaseStock. Recieved FarmSetRequestForCancelOrderDto - {}", farmSetRequestDto.getFarmSetId());
		return farmSetService.increaseStock(farmSetRequestDto);
	}
	
	@PatchMapping(FARM_SET_ORDER)
	public ResponseEntity<String> orderCreate(@RequestBody FarmSetRequestForOrderDto dto,
				@RequestHeader("X-User-Id") String id, @RequestHeader("Authorization") String token){
		log.info("FarmsetController. New order request");
		return farmSetService.reserveFarmSet(dto, id);
		
	}
}
