package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/{userId}")
    public EducationDto addEducation(@PathVariable long userId,
                                    @Valid @RequestBody EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(@PathVariable long userId,
                                        @RequestBody EducationDto educationDto) {

            return educationService.updateEducation(userId, educationDto);
    }

}

