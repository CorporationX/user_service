package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalServiceValidator {
    private static final int MAX_USER_GOALS_LIMIT = 3;
    private final SkillService skillService;

    public void validateUserGoalLimit(final int activeGoalCount) {
        validateCondition(
                activeGoalCount >= MAX_USER_GOALS_LIMIT,
                "This user has exceeded the goal limit"
        );
    }

    public void validateGoalsExist(final Stream<Goal> goals) {
        validateCondition(goals.toList().isEmpty(), "A goal with this ID does not exist");
    }

    public void validateGoalStatusNotCompleted(final Goal goal) {
        validateCondition(
                goal.getStatus() == GoalStatus.COMPLETED,
                "The goal cannot be updated because it is already completed"
        );
    }

    public void validateSkillsExistByTitle(final List<Skill> skills) {
        validateCondition(
                !skillService.existsByTitle(skills),
                "There is no skill with this name"
        );
    }

    private void validateCondition(boolean condition, String errorMessage) {
        if (condition) {
            throw new DataValidationException(errorMessage);
        }
    }
}
