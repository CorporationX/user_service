package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private static int GOALS_MAX_NUM = 3;

    private final UserRepository userRepository;

    private final GoalRepository goalRepository;

    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationMapperImpl goalInvitationMapper;

    public void validateControllerInputData(GoalInvitationDto invitation) {
        if (isInvalidId(invitation.getGoalId())) {
            throw new DataValidationException("Goal Id is invalid");
        }
        if (isInvalidId(invitation.getInviterId()) || isInvalidId(invitation.getInvitedUserId())) {
            throw new DataValidationException("User Id(s) is invalid");
        }
    }

    public void validateId(long id) {
        if (isInvalidId(id)) {
            throw new DataValidationException("Id is invalid");
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

    public void validateAcceptedGoalInvitation(long invitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new DataValidationException("Goal invitation not found"));
        GoalInvitationDto goalInvitationDto = goalInvitationMapper.toDto(goalInvitation);
        if (!goalRepository.existsById(goalInvitationDto.getGoalId())) {
            throw new DataValidationException(("Goal not found"));
        }
        if (goalRepository.countActiveGoalsPerUser(goalInvitationDto.getInvitedUserId()) >= GOALS_MAX_NUM) {
            throw new DataValidationException(("User has reached maximum number of goals"));
        }
    }

    private boolean isInvalidId(Long id) {
        return id == null || id < 0;
    }
}
