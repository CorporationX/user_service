package school.faang.user_service.service.education;

import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;

public interface EducationService {
    EducationDto addEducation(long userId, CreateEducationDto dto);

    EducationDto updateEducation(long userId, long educationId, UpdateEducationDto dto);

    EducationDto getById(long educationId);
}