package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.EducationDto.AddEducationDto;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.exception.common.DataValidationException;
import school.faang.user_service.mapper.education.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;
import java.util.Objects;

import static school.faang.user_service.util.ValidationUtils.executeIfNotNull;

@RequiredArgsConstructor
@Service
public class EducationService {


    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final UserContext userContext;


    public EducationDto addEducation(AddEducationDto addEducationDto) {
        if (addEducationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom cannot be greater than the current year.");
        }
        long userId = userContext.getUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));

        Education education = educationMapper.toEducation(addEducationDto);
        education.setUser(user);

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public Education updateEducation(long educationId, Education newEducationData) {
        if (newEducationData.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom cannot be greater than the current year.");
        }

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education not found"));


        if (Objects.nonNull(education.getUser()) && education.getUser().getId() != userContext.getUserId()) {
            throw new DataValidationException("User does not have permission to update this education record.");
        }

        executeIfNotNull(newEducationData.getYearFrom(),
                () -> education.setYearFrom(newEducationData.getYearFrom()));

        executeIfNotNull(newEducationData.getYearTo(),
                () -> education.setInstitution(newEducationData.getInstitution()));

        executeIfNotNull(newEducationData.getEducationLevel(),
                () -> education.setEducationLevel(newEducationData.getEducationLevel()));

        executeIfNotNull(newEducationData.getYearTo(),
                () -> education.setYearTo(newEducationData.getYearTo()));

        executeIfNotNull(newEducationData.getSpecialization(),
                () -> education.setSpecialization(newEducationData.getSpecialization()));

        return educationRepository.save(education);
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education not found"));
        return educationMapper.toEducationDto(education);
    }
}