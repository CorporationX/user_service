package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.service.EducationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(@PathVariable long userId, @Valid @RequestBody EducationDto educationDto) {
        log.info("Received request to add education for user with id: {}", userId);
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(@PathVariable long userId, @Valid @RequestBody EducationDto educationDto) {
        log.info("Received request to update education with id: {} for user with id: {}", educationDto.id(), userId);
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        log.info("Received request to fetch education with id: {}", educationId);
        return educationService.getById(educationId);
    }
}