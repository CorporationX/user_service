package school.faang.user_service.controller.career;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.CareerService;

@RestController("/api/v1/career")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @PostMapping
    public CareerDto addCareer(long userId, @Valid @RequestBody CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping
    public CareerDto updateCareer(long userId,@Valid @RequestBody CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
