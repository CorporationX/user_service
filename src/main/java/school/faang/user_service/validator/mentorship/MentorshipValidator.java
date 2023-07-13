package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.UserValidator;

@Component
@RequiredArgsConstructor
public class MentorshipValidator {
    private final UserValidator userValidator;

    public void validateToDeleteMentee(long mentorId, long menteeId) {
        User mentor = userValidator.validateUser(mentorId);
        User mentee = userValidator.validateUser(menteeId);
        if (mentor.getMentees().stream().noneMatch(m -> m.getId() == mentee.getId())) {
            throw new RuntimeException("Mentor with id: " + mentorId + " does not have mentee with id: " + menteeId);
        }
    }
}
