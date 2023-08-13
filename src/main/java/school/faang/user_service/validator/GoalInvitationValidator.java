package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

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

    private boolean isInvalidId(Long id) {
        return id == null || id < 0;
    }
}
