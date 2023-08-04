package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.tasksEntity.SameEntityException;

@Component
public class GoalInvitationControllerValidator {
    public void checkInviterAndInvitedAreTheSame(GoalInvitationDto dto) {
        if (dto.inviterId().equals(dto.invitedUserId())) {
            throw new SameEntityException("Inviter and invited are the same");
        }
    }
}
