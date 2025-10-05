package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.career.CareerService;

@RequiredArgsConstructor
@Component
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    public CareerDto addCareer(CreateCareerDto careerDto) {
        validateString(careerDto.company(), "company");
        validateString(careerDto.position(), "position");
        validateNotNull(careerDto.from(), "from date");
        return careerService.addCareer(userContext.getUserId(), careerDto);
    }

    public CareerDto updateCareer(long careerId, UpdateCareerDto careerDto) {
        validateString(careerDto.company(), "company");
        validateString(careerDto.position(), "position");
        validateNotNull(careerDto.from(), "from date");
        return careerService.updateCareer(userContext.getUserId(), careerId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}