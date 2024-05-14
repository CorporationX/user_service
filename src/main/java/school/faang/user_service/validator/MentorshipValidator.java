package school.faang.user_service.validator;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MentorshipValidator {
    public void validateMentorshipIds(Long mentorId, Long menteeId) {
        if (mentorId == menteeId) {
            throw new ValidationException("Same mentorId and menteeId");
        }
    }
}
