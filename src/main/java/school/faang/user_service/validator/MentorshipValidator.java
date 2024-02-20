package school.faang.user_service.validator;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MentorshipValidator {
    public void validateMentorshipIds(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new ValidationException("MentorId and MenteeId cannot be the same");
        }
    }
}