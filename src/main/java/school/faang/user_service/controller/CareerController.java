package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/careers")
public class CareerController {
    private final CareerService careerService;

    @PostMapping("/userId")
    public CareerDto addCareer(@PathVariable long userId,@RequestBody CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }
}
