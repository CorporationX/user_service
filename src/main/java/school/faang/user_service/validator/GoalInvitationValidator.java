package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private final UserRepository userRepository;

    public void validateControllerInputData(GoalInvitationDto invitation) {
        if (isInvalidId(invitation.getGoalId())) {
            throw new DataValidationException("Goal Id is invalid");
        }
        if (isInvalidId(invitation.getInviterId()) || isInvalidId(invitation.getInvitedUserId())) {
            throw new DataValidationException("User Id(s) is invalid");
        }
    }

    public void validateGoalInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidationException("Users cannot send invitations to themselves");
        }
        if (!userRepository.existsById(invitation.getInviterId()) ||
                !userRepository.existsById(invitation.getInvitedUserId())) {
            throw new DataValidationException("Only existing users can send or accept invitations");
        }
    }

    private boolean isInvalidId(Long id) {
        return id == null || id < 0;
    }
}
