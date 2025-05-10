package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }
}
