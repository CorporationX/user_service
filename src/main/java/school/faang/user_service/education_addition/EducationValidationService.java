package school.faang.user_service.education_addition;

import school.faang.user_service.entity.Education;

public interface EducationValidationService {

    void validateOnAdd(long userId, EducationDto educationDto) throws DataValidationException;

    void validateOnUpdate(long userId, Education education, EducationDto educationDto) throws DataValidationException;

    void validateOnGetById(long educationId) throws DataValidationException;

}
