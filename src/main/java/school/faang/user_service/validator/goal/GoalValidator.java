package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private static final int MAX_USER_GOALS_LIMIT = 3;

    private final GoalRepository goalRepository;

    public void validateGoalIdIsPositiveAndNotNull(Long goalId) {
        if (goalId == null) {
            throw new ValidationException("Goal id can't be null");
        }
        if (goalId < 0) {
            throw new ValidationException("Goal id can't be less than 0");
        }
    }

    public void validateGoalWithIdIsExisted(Long goalId) {
        goalRepository.findById(goalId)
                .orElseThrow(() -> new ValidationException("Goal id " + goalId + " not exists"));
    }

    public void validateUserActiveGoalsAreLessThenIncoming(Long userId, int goalMaxCount) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= goalMaxCount) {
            throw new ValidationException("User " + userId + " has max active goals");
        }
    }

    public void validateUserNotWorkingWithGoal(Long userId, Long goalId) {
        goalRepository.findGoalsByUserId(userId)
                .mapToLong(Goal::getId)
                .filter(goal -> goal == goalId)
                .findAny()
                .ifPresent(goal -> {
                    throw new ValidationException("User with id " + userId + " already has goal with id " + goalId);
                });
    }

    public void validateUserGoalLimit(Long userId) {
        int activeGoalCount = goalRepository.countActiveGoalsPerUser(userId);

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
