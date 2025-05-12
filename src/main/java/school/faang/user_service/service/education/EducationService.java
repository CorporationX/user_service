package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.adapter.EducationRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final EducationRepositoryAdapter educationRepositoryAdapter;
    private final EducationMapper educationMapper;

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        User user = userRepositoryAdapter.getById(userId);

        Education education = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(user)
                .build();

        return educationMapper.toEducationDto(educationRepositoryAdapter.save(education));
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        Education education = educationRepositoryAdapter.getById(educationDto.getId());

        if (!Objects.equals(education.getUser().getId(), userId)) {
            throw new DataValidationException("You can only update your own education");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto)
                .toBuilder()
                .user(education.getUser())
                .build();

        return educationMapper.toEducationDto(educationRepositoryAdapter.save(updatedEducation));
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepositoryAdapter.getById(educationId);

        return educationMapper.toEducationDto(education);
    }

}
