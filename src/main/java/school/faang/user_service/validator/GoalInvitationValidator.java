package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private static final int GOALS_MAX_NUM = 3;

    @Autowired
    private final UserValidator userValidator;

    public void validateControllerInputData(GoalInvitationDto invitation) {
        if (isInvalidId(invitation.getGoalId())) {
            throw new DataValidationException("Goal Id is invalid");
        }
        if (isInvalidId(invitation.getInviterId()) || isInvalidId(invitation.getInvitedUserId())) {
            throw new DataValidationException("User Id(s) is invalid");
        }
    }

    public void validateId(long id) {
        if (isInvalidId(id)) {
            throw new DataValidationException("Id is invalid");
        }
    }

    public void validateNewGoalInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidationException("Users cannot send invitations to themselves");
        }
        userValidator.validateUserId(invitation.getInviterId());
        userValidator.validateUserId(invitation.getInvitedUserId());
    }

    public void validateAcceptedGoalInvitation(GoalInvitation goalInvitation) {
        if (!goalInvitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new DataValidationException("Invitation cannot be accepted");
        }

        Goal goal = goalInvitation.getGoal();
        User invitedUser = goalInvitation.getInvited();
        if (goal == null || invitedUser == null) {
            throw new EntityNotFoundException("Goal or user not found");
        }

        List<Goal> currentUserGoals = invitedUser.getGoals();
        if (currentUserGoals.size() >= GOALS_MAX_NUM) {
            throw new DataValidationException("User has reached maximum number of goals");
        }
        if (currentUserGoals.contains(goal)) {
            throw new DataValidationException("User already has this goal");
        }
    }

    public void validateRejectedGoalInvitation(GoalInvitation goalInvitation) {
        if (!goalInvitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new DataValidationException("Invitation cannot be rejected");
        }
        if (goalInvitation.getGoal() == null) {
            throw new EntityNotFoundException("Goal not found");
        }
    }

    public void validateFilter(GoalInvitationFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null");
        }
    }

    private boolean isInvalidId(Long id) {
        return id == null || id < 0;
    }
}
