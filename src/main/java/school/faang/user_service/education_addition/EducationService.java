package school.faang.user_service.education_addition;

public interface EducationService {

    EducationDto addEducation(long userId, EducationDto educationDto) throws DataValidationException;

    EducationDto updateEducation(long userId, long educationId, EducationDto educationDto) throws DataValidationException;

    EducationDto getById(long educationId) throws DataValidationException;
}