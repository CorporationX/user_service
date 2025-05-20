package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.mapper.EducationMapper;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public void checkYearFrom(EducationDto educationDto) throws DataValidationException {
        if (!(educationDto.getYearFrom().compareTo(LocalDate.now().getYear()) > 0)) {
            return;
        }
        throw new DataValidationException("Your year is greater than the current year.");
    }

    public User checkUserIdEmpty(Long userId) throws DataValidationException {
        return userRepository.findById(userId)
            .orElseThrow(() -> new DataValidationException("User with ID " + userId + " not found"));
    }

    public void checkUserIdNull(Long userId) throws DataValidationException {
        if (userId == null) {
            throw new DataValidationException("User ID is null.");
        }
    }

    public EducationDto saveEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        checkUserIdNull(userId);
        User user = checkUserIdEmpty(userId);

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education resultEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(resultEducation);
    }

    public EducationDto addEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        return saveEducation(userId, educationDto);
    }

    public EducationDto updateEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        checkUserIdNull(userId);

        if (userId != educationDto.getId()) {
            throw new DataValidationException("User ID in the path does not match the user ID in the request body");
        }

        return saveEducation(userId, educationDto);
    }

    public EducationDto getById(Long educationId) throws DataValidationException {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education with ID " + educationId + " not found"));
        return educationMapper.toEducationDto(education);
    }
}
