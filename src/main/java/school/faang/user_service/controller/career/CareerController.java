package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.service.career.CareerService;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    public CareerDto addCareer(CreateCareerDto careerDto) {
        return careerService.addCareer(userContext.getUserId(), careerDto);
    }

    public CareerDto updateCareer(long careerId, UpdateCareerDto careerDto) {
        return careerService.updateCareer(userContext.getUserId(), careerId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}