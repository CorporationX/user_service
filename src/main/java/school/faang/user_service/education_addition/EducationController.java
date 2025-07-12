package school.faang.user_service.education_addition;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/education")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping
    public EducationDto addEducation(@PathVariable long userId,
                                     @RequestBody EducationDto educationDto) throws DataValidationException {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{educationId}")
    public EducationDto updateEducation(@PathVariable long userId,
                                        @PathVariable long educationId,
                                        @RequestBody EducationDto educationDto) throws DataValidationException {
        return educationService.updateEducation(userId, educationId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getEducationById(@PathVariable long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}