package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class MentorshipValidator {
    public void idValidator(long id) {
        if (id < 1) {
            throw new DataValidationException("Id must be more 1, it's wrong id");
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
