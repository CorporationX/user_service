package school.faang.user_service.service.education;

import school.faang.user_service.dto.user.education.EducationDto;

public interface EducationService {
    EducationDto addEducation (long userId, EducationDto educationDto);

    EducationDto updateEducation (long userId, long educationId, EducationDto educationDto);

    EducationDto getById(long educationId);
}
