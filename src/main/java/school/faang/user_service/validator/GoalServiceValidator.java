package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class GoalServiceValidator {

    private static final int MAX_USER_GOALS_LIMIT = 3;

    public void validateUserGoalLimit(final int activeGoalCount) {
        validateCondition(
                activeGoalCount >= MAX_USER_GOALS_LIMIT,
                "This user has exceeded the goal limit"
        );
    }

    public void validateGoalStatusNotCompleted(final Goal goal) {
        validateCondition(
                goal.getStatus() == GoalStatus.COMPLETED,
                "The goal cannot be updated because it is already completed"
        );
    }

    private void validateCondition(boolean condition, String errorMessage) {
        if (condition) {
            throw new DataValidationException(errorMessage);
        }
    }
}
