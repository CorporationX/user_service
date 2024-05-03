package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle().isBlank()) {
            throw new DataValidationException("Goals title must exists");
        }
        goalService.createGoal(userId, goal);
    }
}
