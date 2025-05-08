package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        log.info("Adding education for userId={} with data={}", userId, educationDto);

        validateYears(educationDto.getYearFrom(), educationDto.getYearTo());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId={}", userId);
                    return new DataValidationException("User not found");
                });

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education savedEducation = educationRepository.save(education);

        log.info("Education added successfully with id={}", savedEducation.getId());

        return educationMapper.toEducationDto(savedEducation);
    }

    @Transactional
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        log.info("Updating education for userId={} with data={}", userId, educationDto);

        validateYears(educationDto.getYearFrom(), educationDto.getYearTo());

        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new DataValidationException("Education not found"));

        if (!Objects.equals(education.getUser().getId(), userId)) {
            log.warn("Unauthorized update attempt by userId={} for educationId={}", userId, educationDto.getId());
            throw new DataValidationException("User does not have permission to update this record");
        }

        educationMapper.updateEducationFromDto(educationDto, education);

        Education updatedEducation = educationRepository.save(education);

        log.info("Education updated successfully for educationId={}", updatedEducation.getId());

        return educationMapper.toEducationDto(updatedEducation);
    }

    public EducationDto getEducationById(long educationId) {
        log.debug("Fetching education by id={}", educationId);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    log.warn("Education record not found for id={}", educationId);
                    return new DataValidationException("Education record not found");
                });
        return educationMapper.toEducationDto(education);
    }

    private void validateYears(Integer yearFrom, Integer yearTo) {
        int currentYear = Year.now().getValue();

        if (yearFrom == null || yearFrom >= currentYear) {
            log.warn("Validation failed: yearFrom={} is not before the current year", yearFrom);
            throw new DataValidationException("YearFrom must be earlier than the current year");
        }
        if (yearTo != null && yearTo < yearFrom) {
            log.warn("Validation failed: yearTo={} is before yearFrom={}", yearTo, yearFrom);
            throw new DataValidationException("YearTo must be equal or after YearFrom");
        }
        if (yearTo != null && yearTo > currentYear) {
            log.warn("Validation failed: yearTo={} is in the future", yearTo);
            throw new DataValidationException("YearTo must not be in the future");
        }
    }
}