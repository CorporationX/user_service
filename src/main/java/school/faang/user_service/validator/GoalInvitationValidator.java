package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class GoalInvitationValidator {

    public void validateControllerInputData(GoalInvitationDto invitation) {
        if (!isValidId(invitation.getGoalId())) {
            throw new DataValidationException("Goal Id is invalid");
        }
        if (!isValidId(invitation.getInviterId()) || !isValidId(invitation.getInvitedUserId())) {
            throw new DataValidationException("User Id(s) is invalid");
        }
    }

    private boolean isValidId(Long id) {
        return id != null && id >= 0;
    }
}
