package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public void createGoal(Long userId, Goal goal) {
        createGoalValidation(userId, goal);
        service.createGoal(userId, goal);
    }

    public void createGoalValidation(Long userId, Goal goal) {
        if (goal == null) {
            throw new DataValidationException("Goal cannot be null");
        }
        if (userId == null || userId < 1) {
            throw new DataValidationException("ID can not be null or less than 1");
        }
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be blank or null");
        }
    }
}
