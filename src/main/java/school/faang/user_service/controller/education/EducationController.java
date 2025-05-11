package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(
            @PathVariable long userId,
            @RequestBody EducationDto educationDto) {

        validateEducationDto(educationDto);
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/update/{userId}")
    public EducationDto updateEducation(
            @PathVariable long userId,
            @RequestBody EducationDto educationDto) {

        validateEducationDto(educationDto);

        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }

    private void validateEducationDto(EducationDto educationDto) {
        int currentYear = LocalDate.now().getYear();

        if (educationDto.getYearFrom() == null || educationDto.getYearFrom() >= currentYear) {
            throw new DataValidationException("Year must be less than current year");
        }
    }

}
