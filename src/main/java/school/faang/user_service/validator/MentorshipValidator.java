package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MentorshipValidator {
    public void validateMenteeAndMentorIds(long menteeId, long mentorId) {
        if (menteeId == mentorId) {
            String errorMessage = String.format("Mentee ID: %s cannot be the same as Mentor ID: %s.", menteeId, mentorId);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}