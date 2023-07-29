package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private final GoalInvitationRepository goalInvitationRepository;

    public void validate(GoalInvitation invitation) {
        Long inviterId = invitation.getInviter().getId();
        Long invitedId = invitation.getInvited().getId();

        if (inviterId.equals(invitedId)) {
            throw new DataValidationException("You can not invite yourself");
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
