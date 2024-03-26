package school.faang.user_service.validation.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

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

        // Условие: в приглашении есть приглашаемы и приглашающие пользователи
        if (inviterId == null || invitedUserId == null) {
            throw new DataValidationException("One of the participants was not indicated in the invitation");
        }
        // Условие: они не один и тот же человек
        if (inviterId.equals(invitedUserId)) {
            throw new DataValidationException("Cannot specify the same ID for the [inviter] and [invitedUser] fields");
        }

        checkingUserInDb(inviterId);
        checkingUserInDb(invitedUserId);
    }

    public void checkingUserInDb(Long userId) {
        // Условие: является ли юзер существующим пользователем в бд
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User with such ID do not exist: " + userId);
        }
    }

    public void validateAcceptGoalInvitation(GoalInvitation invitation) {
        // Условие: максимального количества активных целей (сейчас max = 3)
        if (invitation.getInvited().getGoals().size() >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("The user has reached the goal limit");
        }
        // Условие: пользователь еще не работает над этой целью
        if (invitation.getGoal().getUsers().contains(invitation.getInvited())) {
            throw new DataValidationException("User is already working on a goal");
        }
    }

    public GoalInvitation findInvitation(long id) {
        // Условие: такое приглашение вообще существует?
        return goalInvitationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Invitation with ID: " + id + " does not found"));
    }

    public void validateGoalExists(GoalInvitation invitation) {
        // Условие: такая цель вообще существует?
        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            throw new DataValidationException("There is no such goal");
        }
    }
}