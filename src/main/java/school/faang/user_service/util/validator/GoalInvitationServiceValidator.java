package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.tasksEntity.EntityStateException;
import school.faang.user_service.exception.tasksEntity.TimingException;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.goal.GoalInvitationNotFoundException;
import school.faang.user_service.exception.tasksEntity.notFoundExceptions.goal.GoalNotFoundException;

import java.util.Optional;

@Component
public class GoalInvitationServiceValidator {

    public void validateToCreate(Optional<Goal> goal,
                         Optional<User> inviter,
                         Optional<User> invited) {

        if (goal.isEmpty()) {
            throw new GoalNotFoundException("Goal not found");
        }
        if (inviter.isEmpty()) {
            throw new UserNotFoundException("Inviter not found");
        }
        if (invited.isEmpty()) {
            throw new UserNotFoundException("Invited user not found");
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
            throw new EntityStateException(
                    "Goal invitation is already accepted"
            );
        }
        if (currentInvitation.getInvited().getGoals().size() >= 3) {
            throw new TimingException(
                    "Goal invitation can't be accepted because invited has more than 3 received goal invitations"
            );
        }
        if (currentInvitation.getInvited().getGoals().contains(goalInvitation.get().getGoal())) {
            throw new EntityStateException(
                    "Goal invitation can't be accepted because invited has this goal already"
            );
        }

        return currentInvitation;
    }

    public GoalInvitation validateToReject(Optional<GoalInvitation> goalInvitation) {
        if (goalInvitation.isEmpty()) {
            throw new GoalNotFoundException("Goal invitation with this id not found");
        }
        if (goalInvitation.get().getStatus() == RequestStatus.REJECTED) {
            throw new EntityStateException("Goal invitation is already rejected");
        }

        return goalInvitation.get();
    }
}
