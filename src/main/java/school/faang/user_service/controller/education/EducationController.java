package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public ResponseEntity<EducationDto> addEducation(@PathVariable("userId") long userId, @Valid @RequestBody EducationDto educationDto) {
        EducationDto result = educationService.addEducation(userId, educationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<EducationDto> updateEducation(@PathVariable("userId") long userId, @Valid @RequestBody EducationDto educationDto) {
        EducationDto result = educationService.updateEducation(userId, educationDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<EducationDto> getById(@PathVariable("educationId") long educationId) {
        EducationDto result = educationService.getById(educationId);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}