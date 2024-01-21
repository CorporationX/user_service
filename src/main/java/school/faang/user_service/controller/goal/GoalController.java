package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void deleteGoal(Long goalId) {
        if (goalId == null || goalId <= 0) {
            throw new DataValidationException("Invalid ID: " + goalId);
        }
        goalService.deleteGoal(goalId);

    }

    public void createGoal(Long userId, Goal goal) {
        if (userId == null || userId <= 0) {
            throw new DataValidationException("Invalid ID: " + userId);
        }
        if (goal == null || goal.getTitle() == null || goal.getTitle().isEmpty() || goal.getTitle().isBlank()) {
            throw new DataValidationException("goal cannot be null or empty");
        }
        goalService.createGoal(userId, goal);
    }
}