package school.faang.user_service.validator.mentorship;

import school.faang.user_service.exception.DataValidationException;

public class MentorshipValidator {
    public void idValidator(long id) {
        if (id < 1) {
            throw new DataValidationException("Not right id");
        }
    }
}
