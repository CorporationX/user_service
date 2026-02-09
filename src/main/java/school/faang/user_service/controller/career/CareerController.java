package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.career.CareerService;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class CareerController {

    private CareerService careerService;

    private UserContext userContext;

    public CareerDto addCareer(long id, CareerDto careerDto) {
        validateNotNull(careerDto.getFrom(), careerDto.getCompany(), careerDto.getPosition());
        return careerService.addCareer(userContext.getUserId(), careerDto);
    }

    public void validateNotNull(LocalDate from, String company, String position) {
        if (from == null || company == null || position == null) {
            throw new DataValidationException("Error");
        }
    }

    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto) {
        validateNotNull(careerDto.getFrom(), careerDto.getCompany(), careerDto.getPosition());
        return careerService.updateCareer(userId, careerId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
