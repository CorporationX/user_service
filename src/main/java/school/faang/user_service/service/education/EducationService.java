package school.faang.user_service.service.education;

import school.faang.user_service.dto.EducationDto;

public interface EducationService {
    EducationDto addEducation(long userId, EducationDto educationDto);

    EducationDto updateEducation(long userId, EducationDto educationDto);

    EducationDto getEducationById(long educationId);
}