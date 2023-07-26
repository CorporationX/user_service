package school.faang.user_service.validator.mentorship;

import school.faang.user_service.exception.DataValidationException;

public class MentorshipValidator {
    public void idValidator(long id) {
        if (id < 0) {
            throw new DataValidationException("Id mast be bore 0");
        }
    }

    public void equalsIdValidator(long firstId, long secondId) {
        idValidator(firstId);
        idValidator(secondId);
        if (firstId == secondId) {
            throw new DataValidationException("Id is equals, it is one person");
        }
    }
}
