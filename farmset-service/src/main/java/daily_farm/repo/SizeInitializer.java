package daily_farm.repo;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import daily_farm.entity.FarmSet;
import daily_farm.entity.FarmSetSize;


@Component
public class SizeInitializer {

	@Value("${farmset.sizes.list}")
	private List<String> LIST_OF_FARM_SET_SIZES;
	
	FarmSetSizeRepository sizeRepo;
	public SizeInitializer(FarmSetSizeRepository sizeRepo) {
        this.sizeRepo = sizeRepo;
      
    }
	

	@PostConstruct
	public void init() {
		if (sizeRepo.count() == 0) {
			List<FarmSetSize> initList = LIST_OF_FARM_SET_SIZES.stream().map(size -> FarmSetSize.builder()
					.size(size)
					.farmSets(new HashSet<FarmSet>())
					.build()).toList();
			sizeRepo.saveAll(initList);
		}
	}
}
