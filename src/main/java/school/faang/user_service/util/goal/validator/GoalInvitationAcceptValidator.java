package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.util.goal.exception.AcceptingGoalInvitationException;
import school.faang.user_service.util.goal.exception.GoalInvitationNotFoundException;

import java.util.Optional;

@Component
public class GoalInvitationAcceptValidator {

    public GoalInvitation validateRequestToAccept(Optional<GoalInvitation> goalInvitation) {
        if (goalInvitation.isEmpty()) {
            throw new GoalInvitationNotFoundException(
                    "Goal invitation with id = " + goalInvitation.get().getId() + " not found"
            );
        }
        if (goalInvitation.get().getInvited().getGoals().size() >= 2) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has more than 2 received goal invitations"
            );
        }
        if (goalInvitation.get().getInvited().getGoals().contains(goalInvitation.get().getGoal())) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has this goal already"
            );
        }

        return goalInvitation.get();
    }
}
