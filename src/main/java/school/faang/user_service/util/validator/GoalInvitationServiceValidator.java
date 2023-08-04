package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.AcceptingGoalInvitationException;
import school.faang.user_service.exception.GoalInvitationNotFoundException;
import school.faang.user_service.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.exception.RejectionGoalInvitationException;

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

        var currentInvitation = goalInvitation.get();

        if (currentInvitation.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation is already accepted"
            );
        }
        if (currentInvitation.getInvited().getGoals().size() >= 3) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has more than 3 received goal invitations"
            );
        }
        if (currentInvitation.getInvited().getGoals().contains(goalInvitation.get().getGoal())) {
            throw new AcceptingGoalInvitationException(
                    "Goal invitation can't be accepted because invited has this goal already"
            );
        }

        return currentInvitation;
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
