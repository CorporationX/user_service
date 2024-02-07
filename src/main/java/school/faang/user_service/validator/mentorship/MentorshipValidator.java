package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;

@Component
public class MentorshipValidator {
    public void validateMentorshipIds(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new IllegalArgumentException("MentorId and MenteeId cannot be the same");
        }
    }
}