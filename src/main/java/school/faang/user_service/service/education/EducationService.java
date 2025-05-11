package school.faang.user_service.service.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        User user = fetchUser(userId);

        Education education = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(user)
                .build();

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        Education education = fetchEducation(educationDto.getId());

        if (!education.getUser().getId().equals(userId)) {
            throw new DataValidationException("You can only update your own education");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(education.getUser())
                .build();

        return educationMapper.toEducationDto(educationRepository.save(updatedEducation));
    }

    public EducationDto getById(long educationId) {
        Education education = fetchEducation(educationId);

        return educationMapper.toEducationDto(education);
    }

    private User fetchUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with id=" + userId));
    }

    private Education fetchEducation(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Education not found with id=" + educationId));
    }
}
