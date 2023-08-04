package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidation {

    private final GoalRepository goalRepository;

    public void invitationValidationUser(GoalInvitationDto invitation) {
        Long inviterId = invitation.getInviterId();
        Long invitedUserId = invitation.getInvitedUserId();

        EmptyUsers(inviterId, invitedUserId);
        SameUsers(inviterId, invitedUserId);
        ExistUsers(inviterId, invitedUserId);
    }

    private void EmptyUsers(Long inviterId, Long invitedUserId) {
        if (Objects.isNull(inviterId) || Objects.isNull(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Invited or inviting users is empty");
        }
    }
    private void SameUsers(Long inviterId, Long invitedUserId) {
        if (inviterId.equals(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Inviter and invited users must be different");
        }
    }
    private void ExistUsers(Long inviterId, Long invitedUserId) {
        if (!goalRepository.existsById(inviterId) || !goalRepository.existsById(invitedUserId)) {
            throw new EntityNotFoundException("Invalid request. Requester user not found");
        }
    }
}
