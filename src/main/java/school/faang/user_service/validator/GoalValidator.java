package school.faang.user_service.validator;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

public class GoalValidator {
    public static final int MAX_ACTIVE_GOALS = 3;

    public static void validateId(Long id, String entityName) {
        if (id == null) {
            throw new DataValidationException(entityName + " id cannot be null!");
        }
        if (id <= 0) {
            throw new DataValidationException(entityName + " id cannot be less than 1!");
        }
    }

    public static void validateGoal(GoalDto goalDto) {
        if (goalDto == null) {
            throw new DataValidationException("Goal cannot be null!");
        }
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title of goal cannot be empty!");
        }
    }

    public static void validateAdditionGoalToUser(User user, GoalDto goalDto) {
        if (user.getGoals() == null || user.getGoals().isEmpty() || goalDto.getStatus() == GoalStatus.COMPLETED) {
            return;
        }

        List<Goal> activeGoals = user.getGoals().stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .toList();

        if (activeGoals.size() >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("User cannot have more than " + MAX_ACTIVE_GOALS + " active goals at the same time!");
        }
    }

    public static void validateUpdatingGoal(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("You cannot update a completed goal!");
        }
    }
}
