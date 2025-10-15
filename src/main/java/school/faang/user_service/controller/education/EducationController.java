package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/api/v1/educations")
@RequiredArgsConstructor
@Slf4j
public class EducationController {
    private final UserContext userContext;
    private final EducationService educationService;

    @PostMapping
    public EducationDto addEducation(@Valid @RequestBody CreateEducationDto dto) {
        long userId = userContext.getUserId();
        return educationService.addEducation(userId, dto);
    }

    @PatchMapping("/{educationId}")
    public EducationDto updateEducation(@PathVariable long educationId, @Valid @RequestBody UpdateEducationDto dto) {
        long userId = userContext.getUserId();
        return educationService.updateEducation(userId, educationId, dto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getEducationById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }
}
