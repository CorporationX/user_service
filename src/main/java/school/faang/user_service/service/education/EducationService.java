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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public EducationDto addEducation(long userId, EducationDto educationDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Education education = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(user)
                .build();

        Education savedEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(savedEducation);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {

        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!education.getUser().getId().equals(userId)) {
            throw new DataValidationException("You can only update your own education");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(education.getUser())
                .build();

        Education savedEducation = educationRepository.save(updatedEducation);
        return educationMapper.toEducationDto(savedEducation);
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found"));

        return educationMapper.toEducationDto(education);
    }
}
