package school.faang.user_service.controller.career;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequestMapping
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    public CareerController(CareerService careerService, UserContext userContext) {
        this.careerService = careerService;
        this.userContext = userContext;
    }

    @PostMapping
    public CareerDto addCareer(@RequestBody CareerDto careerDto) {
        if (careerDto.getFrom() == null || careerDto.getCompany() == null || careerDto.getPosition() == null) {
            throw new DataValidationException("Заполните from, company, position");
        }
        long userId = userContext.getUserId();

        return careerService.addCareer(userId, careerDto);
    }

    public CareerDto updateCareer(
            @PathVariable long careeId,
            @RequestBody CareerDto careerDto) {
        if (careerDto.getFrom() == null || careerDto.getCompany() == null || careerDto.getPosition() == null) {
            throw new DataValidationException("Заполните from, company и position");
        }

        long userId = userContext.getUserId();

        return careerService.updateCareer(userId, careeId, careerDto);
    }

    @GetMapping
    public CareerDto getById(@PathVariable long careerId) {
        return careerService.getById(careerId);
    }
}
