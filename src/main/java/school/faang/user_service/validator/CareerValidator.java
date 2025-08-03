package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CareerValidator {

    public void validate(CareerDto careerDto) {
        if (careerDto.getFrom() == null) {
            throw new DataValidationException("Start date is required");
        }
        if (careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("Start date cannot be in the future");
        }
        if (careerDto.getTo() != null && careerDto.getFrom().isAfter(careerDto.getTo())) {
            throw new DataValidationException("End date cannot be before start date");
        }
        if (careerDto.getCompany() == null || careerDto.getCompany().isBlank()) {
            throw new DataValidationException("Company name is required");
        }
        if (careerDto.getPosition() == null || careerDto.getPosition().isBlank()) {
            throw new DataValidationException("Position is required");
        }
    }
}
