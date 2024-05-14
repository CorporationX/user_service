package school.faang.user_service.validator;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MentorshipValidator {
    public void validateMentorshipIds(Long mentorId, Long menteeId) {
        if (Objects.equals(mentorId, menteeId)) {
            throw new ValidationException("Same mentorId and menteeId");
        }
    }
}
