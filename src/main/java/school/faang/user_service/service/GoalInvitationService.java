package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@AllArgsConstructor
@Service
public class GoalInvitationService {
    private GoalInvitationRepository goalInvitationRepository;

    private UserRepository userRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId() == invitation.getInvitedUserId()) {
            throw new RuntimeException("Users cannot send invitations to themselves");
        }
        if (!userRepository.isUserPresent(invitation.getInviterId()) ||
                !userRepository.isUserPresent(invitation.getInvitedUserId())) {
            throw new RuntimeException("Only existing users can send or accept invitations");
        }
        goalInvitationRepository.create(invitation.getGoalId(), invitation.getInviterId(), invitation.getInvitedUserId());
    }
}
