package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    private final UserRepository userRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId() == invitation.getInvitedUserId()) {
            throw new DataValidationException("Users cannot send invitations to themselves");
        }
        if (!userRepository.isUserPresent(invitation.getInviterId()) ||
                !userRepository.isUserPresent(invitation.getInvitedUserId())) {
            throw new DataValidationException("Only existing users can send or accept invitations");
        }
        goalInvitationRepository.create(invitation.getGoalId(), invitation.getInviterId(), invitation.getInvitedUserId());
    }
}
