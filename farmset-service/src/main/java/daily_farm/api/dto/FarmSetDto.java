package daily_farm.api.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import static daily_farm.api.messages.FarmSetErrorMessages.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FarmSetDto {

	@NotBlank(message = SIZE_NOT_NULL_VALUE)
	String size;
	
	@NotBlank(message = CATEGORY_NOT_NULL_VALUE)
	String category;
	
	@Length(max = 200, message = LONG_DESCRIPTION)
	String description;
	
	@Positive(message = PRICE_IS_NOT_POSITIVE)
	double price;
	
	@Positive(message = COUNT_IS_NOT_POSITIVE)
	int availibleCount;
	
	
	boolean abailible;
	
	@NotNull(message = DATE_NOT_NULL_VALUE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime pickupTimeStart;
	
	@NotNull(message = DATE_NOT_NULL_VALUE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime pickupTimeEnd;
	 
	@AssertTrue(message = WRONG_DATES)
    public boolean isPickupTimeValid() {
        if (pickupTimeStart == null || pickupTimeEnd == null) {
            return true; 
        }
        return pickupTimeStart.isBefore(pickupTimeEnd);
    }
	
}
