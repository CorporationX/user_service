package school.faang.user_service.service.education;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;

@Service
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public EducationService(UserRepository userRepository, EducationRepository educationRepository, EducationMapper educationMapper) {
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.educationMapper = educationMapper;
    }

    @SneakyThrows
    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("YearFrom must be less than the current year");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    @SneakyThrows
    @Transactional
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("YearFrom must be less than the current year");
        }

        Education education = getById(educationDto.getId());

        if (education.getUser().getId() != userId) {
            throw new DataValidationException("User does not have permission to update this record");
        }

        education.setYearFrom(educationDto.getYearFrom());
        education.setYearTo(educationDto.getYearTo());
        education.setInstitution(educationDto.getInstitution());
        education.setEducationLevel(educationDto.getEducationLevel());
        education.setSpecialization(educationDto.getSpecialization());

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    @SneakyThrows
    public Education getById(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education record not found"));
    }
}