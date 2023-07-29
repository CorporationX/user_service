package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository repository;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidation(invitation);
//        repository.save(invitation)
    }

    private void goalInvitationValidation(GoalInvitationDto invitation) {
        Long inviterId = invitation.getInviterId();
        Long invitedUserId = invitation.getInvitedUserId();

        if (Objects.isNull(inviterId) || Objects.isNull(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Invited or inviting users is empty");
        }
        if (inviterId.equals(invitedUserId)) {
            throw new IllegalArgumentException("Invalid request. Inviter and invited users must be different");
        }
        if (!repository.existsById(inviterId) || !repository.existsById(invitedUserId)) {
            throw new EntityNotFoundException("Invalid request. Requester user not found");
        }
    }
}
