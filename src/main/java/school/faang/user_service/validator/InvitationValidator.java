package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvitationValidator {
    private final UserValidator userValidator;
    private final GoalService goalService;

    public void validateIdEquality(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new DataValidationException("Id of inviter and invited user can't be the same");
        }
    }

    public void validateUsersExistence(Long userId1, Long userId2) {
        userValidator.areUsersExist(userId1, userId2);
    }

    public void validateUserGoalsAmount(Long id) {
        goalService.validateUserGoalsAmount(id);
    }

    public void validateGoalExistence(Long id) {
        goalService.getGoalById(id);
    }

    public void validateGoalAlreadyPicked(Long id) {
        List<Goal> userGoals = goalService.getGoalsByUserId(id).toList();
        if (userGoals.contains(goalService.getGoalById(id))) {
            throw new IllegalStateException("User already has this goal");
        }
    }
}
