package school.faang.user_service.util.goal.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.util.goal.exception.RejectionGoalInvitationException;

import java.util.Optional;

@Component
public class GoalInvitationRejectValidator {

    public GoalInvitation validateRequest(Optional<GoalInvitation> goalInvitation) {
        if (goalInvitation.isEmpty()) {
            throw new RejectionGoalInvitationException("Goal invitation with this id not found");
        }
        if (goalInvitation.get().getStatus() == RequestStatus.REJECTED) {
            throw new RejectionGoalInvitationException("Goal invitation is already rejected");
        }

        return goalInvitation.get();
    }
}
