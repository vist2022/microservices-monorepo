package daily_farm.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import daily_farm.api.dto.*;

public interface IFarmSetService {

	ResponseEntity<Void> addFarmSet(UUID id, FarmSetDto farmSetDto);

	ResponseEntity<List<FarmSetResponseDto>> getAbailableFarmSetsForFarmer(UUID id);

	ResponseEntity<List<FarmSetResponseDto>> getAllFarmSets();

	ResponseEntity<FarmSetResponseForOrderDto> decreaseStock(FarmSetRequestForOrderDto farmSetRequestDto);

	ResponseEntity<Void> increaseStock(FarmSetRequestForCancelOrderDto farmSetRequestDto);



}
