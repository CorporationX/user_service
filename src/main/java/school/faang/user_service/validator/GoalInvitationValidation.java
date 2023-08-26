package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidation {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public void invitationValidationUser(GoalInvitationDto invitation) {
        Long inviterId = invitation.getInviterId();
        Long invitedUserId = invitation.getInvitedUserId();
        Long goalId = invitation.getGoalId();

        existUsers(inviterId, invitedUserId);
        existGoal(goalId);
        emptyUsers(inviterId, invitedUserId);
        sameUsers(inviterId, invitedUserId);

    }

    private void emptyUsers(Long inviterId, Long invitedUserId) {
        if (Objects.isNull(inviterId) || Objects.isNull(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Invited or inviting users is empty");
        }
    }
    private void sameUsers(Long inviterId, Long invitedUserId) {
        if (inviterId.equals(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Inviter and invited users must be different");
        }
    }
    private void existUsers(Long inviterId, Long invitedUserId) {
        if (!userRepository.existsById(inviterId) || !userRepository.existsById(invitedUserId)) {
            throw new EntityNotFoundException("Invalid request. Requester user not found");
        }
    }
    private void existGoal(Long goalId) {
       if (!goalRepository.existsById(goalId)) {
           throw new EntityNotFoundException("Invalid request. goal not found");
       }
    }
}
