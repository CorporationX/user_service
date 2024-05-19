package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
@Component
public class MentorshipValidator {
    public void addGoalToMenteeFromMentorValidation(User mentee , User mentor) {
        if (mentee.getMentors() == null) {
            throw new NullPointerException("User does not have mentors yet.");
        }
        if (!(mentee.getMentors().contains(mentor))) {
            throw new IllegalArgumentException(String.format("The specified mentor: %s is not in the" +
                    " list of assigned mentors for the user: %s ", mentor.getId(), mentee.getId()));
        }
    }
}