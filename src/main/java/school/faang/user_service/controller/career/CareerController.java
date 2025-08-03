package school.faang.user_service.controller.career;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.career.CareerService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users/{userId}/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CareerDto addCareer(
            @PathVariable long userId,
            @RequestBody CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping("/{careerId}")
    public CareerDto updateCareer(
            @PathVariable long userId,
            @PathVariable long careerId,
            @RequestBody CareerDto careerDto) {

        if (careerDto.getId() != null && !Objects.equals(careerDto.getId(), careerId)) {
            throw new DataValidationException("ID in path and body must match");
        }

        return careerService.updateCareer(userId, careerDto);
    }

    @GetMapping("/{careerId}")
    public CareerDto getById(@PathVariable long careerId) {
        return careerService.getById(careerId);
    }

}


