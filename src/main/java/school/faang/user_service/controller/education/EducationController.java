package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto.AddEducationDto;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.dto.EducationDto.EducationUpdateDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.mapper.education.EducationMapper;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/api/v1/user/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EducationMapper educationMapper;

    @PostMapping
    public ResponseEntity<EducationDto> addEducation(@RequestBody AddEducationDto addEducationDto) {

        EducationDto addedEducation = educationService.addEducation(addEducationDto);
        return new ResponseEntity<>(addedEducation, HttpStatus.CREATED);

    }

    @PatchMapping("/{educationId}")
    public ResponseEntity<EducationDto> updateEducation(@PathVariable long educationId,
                                                        @RequestBody EducationUpdateDto educationUpdateDto) {
        Education newEducationData = educationMapper.toEducation(educationUpdateDto);
        Education updatedEducation = educationService.updateEducation(educationId, newEducationData);
        EducationDto updatedEducationDto = educationMapper.toEducationDto(updatedEducation);
        return new ResponseEntity<>(updatedEducationDto, HttpStatus.OK);
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<EducationDto> getById(@PathVariable("educationId") long educationId) {
        EducationDto educationDto = educationService.getById(educationId);
        return new ResponseEntity<>(educationDto, HttpStatus.OK);
    }
}