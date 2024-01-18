package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class GoalValidator {
    public void validateFilter(GoalFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null");
        }
    }

    public void validateGoalId(long goalId) {
        if (goalId == 0) {
            throw new DataValidationException("Введите корректный ID");
        }
    }
}
