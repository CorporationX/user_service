package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class MentorshipValidator {
    public void validateMentorshipIds(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new DataValidationException("MentorId and MenteeId cannot be the same");
        }
    }
}