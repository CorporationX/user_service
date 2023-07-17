package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;

@Component
public class GoalInvitationEntityValidator {

    private GoalInvitation goalInvitation = null;

    public void validate(GoalInvitation goalInvitation) {
        this.goalInvitation = goalInvitation;

        checkInviterAndInvitedAreTheSame();
        validateInviter();
        validateInvitedUser();
        validateGoalId();
    }

    private void checkInviterAndInvitedAreTheSame() {
        if (goalInvitation.getInviter().getId() == goalInvitation.getInvited().getId()) {
            throw new MappingGoalInvitationDtoException("Inviter and invited are the same");
        }
    }

    private void validateInviter() {
        if (goalInvitation.getInviter() == null) {
            throw new MappingGoalInvitationDtoException("Inviter wasn't found");
        }
    }

    private void validateInvitedUser() {
        if (goalInvitation.getInvited() == null) {
            throw new MappingGoalInvitationDtoException("Invited user wasn't found");
        }
    }

    private void validateGoalId() {
        if (goalInvitation.getGoal() == null) {
            throw new MappingGoalInvitationDtoException("Goal wasn't found");
        }
    }
}
