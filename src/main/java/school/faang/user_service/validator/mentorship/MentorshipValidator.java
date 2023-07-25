package school.faang.user_service.validator.mentorship;

import school.faang.user_service.exception.DataValidationException;

public class MentorshipValidator {
    public void idValidator(long id) {
        if (id < 0) {
            throw new DataValidationException("Not wrong id");
        }
    }

    public void equalsIdValidator(long firstId, long secondId) {
        if (firstId == secondId) {
            throw new DataValidationException("Id is equals");
        }
    }
}
