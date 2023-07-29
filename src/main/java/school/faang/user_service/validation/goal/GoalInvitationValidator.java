package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;

    public void validate(GoalInvitation invitation) {
        Long inviterId = invitation.getInviter().getId();
        Long invitedId = invitation.getInvited().getId();

        if (inviterId.equals(invitedId)) {
            throw new DataValidationException("Invalid request. You can not invite yourself");
        }
        if (invitation.getInvited().getGoals().size() == MAX_ACTIVE_GOALS) {
            throw new DataValidationException("Invalid request. Inviter user has reached maximum amount of active goals");
        }
        if (invitation.getGoal().getUsers().contains(invitation.getInvited())) {
            throw new DataValidationException("Invalid request. Invited user is already working on this goal");
        }
        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            throw new DataValidationException("Invalid request. Goal does not exists");
        }

        isUserPresent(inviterId);
        isUserPresent(invitedId);
    }

    private void isUserPresent(Long userId) {
        if (!goalInvitationRepository.existsById(userId)) {
            throw new DataValidationException("Invalid request. User with ID: " + userId + " does not exist");
        }
    }
}
