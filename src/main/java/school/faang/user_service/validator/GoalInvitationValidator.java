package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import static school.faang.user_service.entity.RequestStatus.PENDING;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private static final long MAX_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationRepository goalInvitationRepository;

    public void validateCreateInvitation(GoalInvitationDto invitationDto) {
        Long inviterId = invitationDto.getInviterId();
        Long invitedUserId = invitationDto.getInvitedUserId();

        if (inviterId.equals(invitedUserId)) {
            throw new DataValidationException("Cannot specify the same ID:"+ invitedUserId +" for the [inviter] and [invitedUser] fields");
        }

        checkingUserInDb(inviterId);
        checkingUserInDb(invitedUserId);
    }

    public void checkingUserInDb(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User with such ID do not exist: " + userId);
        }
    }

    public void validateAcceptGoalInvitation(GoalInvitation invitation) {
        if (invitation.getInvited().getGoals().size() >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("The user has reached the goal limit");
        }
        if (invitation.getGoal().getUsers().contains(invitation.getInvited())) {
            throw new DataValidationException("User is already working on a goal");
        }
    }

    public void validateGoalExists(GoalInvitation invitation) {
        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            throw new DataValidationException("There is no such goal");
        }
    }

    public void checkingStatusIsPending(GoalInvitation invitation) {
        if (invitation.getStatus()!= PENDING) {
            throw new DataValidationException("The status of the invitation must be PENDING");
        }
    }
}