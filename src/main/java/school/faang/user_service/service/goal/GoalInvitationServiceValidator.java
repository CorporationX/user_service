package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static school.faang.user_service.exception.message.MessageForGoalInvitationService.*;

@Component
public class GoalInvitationServiceValidator {

    private static final int GOAL_SIZE = 3;

    void validateForCreateInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
            throw new DataValidationException(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage());
        }
    }

    List<Goal> validateForAcceptGoalInvitation(User invited, Goal goal) {
        if (invited == null) {
            throw new DataValidationException(NO_INVITED_IN_GOAL_INVITATION.getMessage());
        }

        List<Goal> setGoals = invited.getSetGoals();

        if (setGoals == null) {
            throw new DataValidationException(SET_GOALS_IS_NULL.getMessage());
        }
        if (setGoals.size() > GOAL_SIZE) {
            throw new DataValidationException(MORE_THEN_THREE_GOALS.getMessage());
        }
        if (setGoals.contains(goal)) {
            throw new DataValidationException(INVITED_HAS_GOAL.getMessage());
        }
        return setGoals;
    }
}
