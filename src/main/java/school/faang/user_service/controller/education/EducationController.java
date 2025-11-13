package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;

import school.faang.user_service.dto.user.education.UpdateEducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;
    private final UserContext userContext;

    @PostMapping
    public EducationDto addEducation(@Valid @RequestBody CreateEducationDto educationDto) {

        long userId = userContext.getUserId();
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{educationId}")
    public EducationDto updateEducation(@PathVariable long educationId,
                                        @RequestBody UpdateEducationDto educationDto) {


        long userId = userContext.getUserId();
        return educationService.updateEducation(userId, educationId, educationDto);

    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }


}


