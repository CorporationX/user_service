package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.util.goal.exception.AcceptingGoalInvitationException;
import school.faang.user_service.util.goal.exception.GoalInvitationNotFoundException;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.util.goal.exception.RejectionGoalInvitationException;

import java.util.Optional;

@Component
public class GoalInvitationServiceValidator {

    public void validateToCreate(Optional<Goal> goal,
                         Optional<User> inviter,
                         Optional<User> invited) {

        if (goal.isEmpty()) {
            throw new GoalInvitationNotFoundException("Goal not found");
        }
        if (inviter.isEmpty()) {
            throw new MappingGoalInvitationDtoException("Inviter not found");
        }
        if (invited.isEmpty()) {
            throw new MappingGoalInvitationDtoException("Invited user not found");
        }
    }

    public GoalInvitation validateToAccept(Optional<GoalInvitation> goalInvitation) {
        if (goalInvitation.isEmpty()) {
            throw new GoalInvitationNotFoundException(
                    "Goal invitation with this id not found"
            );
        }
        GoalInvitation currentGoalInvitation = goalInvitation.get();
        int size = currentGoalInvitation.getInvited().getGoals().size();
        if (currentGoalInvitation.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation is already accepted"
            );
        }
        if (size >= 3) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has more than 3 received goal invitations"
            );
        }
        if (currentGoalInvitation.getInvited().getGoals().contains(goalInvitation.get().getGoal())) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has this goal already"
            );
        }

        return currentGoalInvitation;
    }

    public GoalInvitation validateToReject(Optional<GoalInvitation> goalInvitation) {
        if (goalInvitation.isEmpty()) {
            throw new RejectionGoalInvitationException("Goal invitation with this id not found");
        }
        if (goalInvitation.get().getStatus() == RequestStatus.REJECTED) {
            throw new RejectionGoalInvitationException("Goal invitation is already rejected");
        }

        return goalInvitation.get();
    }
}
