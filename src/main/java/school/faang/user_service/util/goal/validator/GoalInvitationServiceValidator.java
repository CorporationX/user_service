package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;

import java.util.Optional;

@Component
public class GoalInvitationServiceValidator {

    public void validate(Optional<Goal> goal,
                         Optional<User> inviter,
                         Optional<User> invited) {

        if (goal.isEmpty()) {
            throw new MappingGoalInvitationDtoException("Goal not found");
        }
        if (inviter.isEmpty()) {
            throw new MappingGoalInvitationDtoException("Inviter not found");
        }
        if (invited.isEmpty()) {
            throw new MappingGoalInvitationDtoException("Invited user not found");
        }
    }
}
