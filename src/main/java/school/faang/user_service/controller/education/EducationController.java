package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    public EducationDto addEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        return educationService.addEducation(userId, educationDto);
    }

    public EducationDto updateEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        return educationService.updateEducation(userId, educationDto);
    }

    public EducationDto getById(Long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}
