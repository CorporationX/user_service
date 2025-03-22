package school.faang.user_service.service.education;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.education.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.service.UserService;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private final UserService userService;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Transactional
    public EducationDto addEducation(@NotNull long userId, @Valid EducationDto educationDto) {
        log.info("Adding education for user with id: {}", userId);

        if (educationDto.yearFrom() > Year.now().getValue()) {
            log.error("Validation failed: YearFrom cannot be in the future");
            throw new DataValidationException("YearFrom cannot be in the future");
        }

        userService.validateUserExists(userId);

        User user = userService.getUserById(userId);
        log.info("User found with id: {}", userId);

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education savedEducation = educationRepository.save(education);
        log.info("Education added successfully with id: {}", savedEducation.getId());

        return educationMapper.toEducationDto(savedEducation);
    }

    @Transactional
    public EducationDto updateEducation(@NotNull long userId, @Valid EducationDto educationDto) {
        log.info("Updating education with id: {} for user with id: {}", educationDto.id(), userId);

        if (educationDto.yearFrom() > Year.now().getValue()) {
            log.error("Validation failed: YearFrom cannot be in the future");
            throw new DataValidationException("YearFrom cannot be in the future");
        }

        if (educationDto.yearTo() != null && educationDto.yearTo() < educationDto.yearFrom()) {
            log.error("Validation failed: YearTo cannot be less than YearFrom");
            throw new DataValidationException("YearTo cannot be less than YearFrom");
        }

        Education existingEducation = educationRepository.findById(educationDto.id())
                .orElseThrow(() -> {
                    log.error("Education not found with id: {}", educationDto.id());
                    return new DataValidationException("Education not found");
                });

        if (existingEducation.getUser().getId() != userId) {
            log.error("User mismatch: Education does not belong to user with id: {}", userId);
            throw new DataValidationException("User mismatch");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto);
        updatedEducation.setUser(existingEducation.getUser());

        Education savedEducation = educationRepository.save(updatedEducation);
        log.info("Education updated successfully with id: {}", savedEducation.getId());

        return educationMapper.toEducationDto(savedEducation);
    }

    public EducationDto getById(long educationId) {
        log.info("Fetching education with id: {}", educationId);

        return educationRepository.findById(educationId)
                .map(education -> {
                    log.info("Education fetched successfully with id: {}", educationId);
                    return educationMapper.toEducationDto(education);
                })
                .orElseThrow(() -> {
                    log.error("Education not found with id: {}", educationId);
                    return new DataValidationException("Education not found");
                });
    }
}