package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

@Component
@RequiredArgsConstructor
public class ValidationInvitation {
    private static final int MAX_GOALS_SIZE = 3;

    public void createInvitation(Long inviterId, GoalInvitationDto invitation) {
        if (inviterId.equals(invitation.getInvitedUserId())) {
            throw new IllegalArgumentException("The user cannot invite himself to the goal");
        }
    }

    public void acceptGoalInvitation(GoalInvitation goalInvitation, User invited, Long invitedId) {
        if (!invitedId.equals(goalInvitation.getInvited().getId())) {
            throw new IllegalArgumentException("Сan only confirm your own invitation");
        }
        if (invited.getGoals().size() >= MAX_GOALS_SIZE) {
            throw new IllegalArgumentException("Users can not accept the invitation, " +
                    "maximum number of active goals (max = 3)");
        }
        if (goalInvitation.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("The invited user is already working on this goal.");
        }
    }

    public void rejectGoalInvitation(GoalInvitation goalInvitation) {
        if (goalInvitation.getStatus() == RequestStatus.PENDING) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Can only cancel an invitation when the status is PENDING");
        }
    }
}