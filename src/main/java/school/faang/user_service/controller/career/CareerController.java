package school.faang.user_service.controller.career;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.service.career.CareerService;

@Slf4j
@RestController
@RequestMapping("/career")
@RequiredArgsConstructor
@Validated
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    @PostMapping
    public CareerDto addCareer(@Valid @RequestBody CreateCareerDto careerDto) {
        Long userId = userContext.getUserId();
        log.info("User {} is adding career: {} at {}", userId, careerDto.position(),
                careerDto.company());
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping("/{careerId}")
    public CareerDto updateCareer(@PathVariable Long careerId,
                                       @Valid @RequestBody UpdateCareerDto careerDto) {
        Long userId = userContext.getUserId();
        log.info("User {} is updating career {}", userId, careerId);
        return careerService.updateCareer(userId, careerId, careerDto);
    }

    @GetMapping("/{careerId}")
    public CareerDto getById(@PathVariable Long careerId) {
        log.info("Retrieved career with id: {}", careerId);
        return careerService.getById(careerId);
    }
}
