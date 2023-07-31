package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;

@Component
public class GoalInvitationControllerValidator {
    public void checkInviterAndInvitedAreTheSame(GoalInvitationDto dto) {
        if (dto.inviterId() == dto.invitedUserId()) {
            throw new MappingGoalInvitationDtoException("Inviter and invited are the same");
        }
    }
}
