package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_ACTIVE_GOALS = 3;

    public void validateActiveGoals(long countActiveGoals) {
        if (countActiveGoals > MAX_ACTIVE_GOALS) {
            throw new DataValidationException("Too many active goals!");
        }
    }

    public void validateExistingSkills(List<Long> userSkillsIds, GoalDto goal) {
        if (!userSkillsIds.containsAll(goal.getSkillIds())) {
            throw new DataValidationException("Not enough skills for the goal!");
        }
    }

    public void validateTitle(GoalDto goal) {
        String title = goal.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Title is empty!");
        }
    }
}
