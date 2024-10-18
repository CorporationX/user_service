package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.goal.GoalInvitationValidationException;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;

    public void validateInvitationToCreate(User inviter, User invited) {
        if (inviter.getId().equals(invited.getId())) {
            throw new GoalInvitationValidationException("Inviter and invited user are the same");
        }
    }

    public void validateInvitationToAccept(GoalInvitation goalInvitation, Goal goal, User invited) {
        if (goal == null) {
            throw new GoalInvitationValidationException("Goal does not exist");
        }
        if (goalRepository.countActiveGoalsPerUser(invited.getId()) >= MAX_ACTIVE_GOALS) {
            throw new GoalInvitationValidationException("User already has the maximum number of active goals");
        }
        if (goalInvitation.getStatus() != RequestStatus.PENDING) {
            throw new GoalInvitationValidationException("Invitation is not pending and cannot be accepted");
        }
        if (goalRepository.findUsersByGoalId(goal.getId()).contains(invited)) {
            throw new GoalInvitationValidationException("User is already assigned to this goal");
        }
    }

    public void validateInvitationToReject(GoalInvitation goalInvitation, Goal goal) {
        if (goal == null) {
            throw new GoalInvitationValidationException("Goal does not exist");
        }
        if (goalInvitation.getStatus() == RequestStatus.REJECTED) {
            throw new GoalInvitationValidationException("Invitation has already been rejected");
        }
    }
}