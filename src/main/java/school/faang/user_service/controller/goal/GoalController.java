package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void deleteGoal(long goalId) {
        if (goalId <= 0) {
            throw new DataValidationException("Invalid ID: " + goalId);
        } else {
            goalService.deleteGoal(goalId);
        }
    }
}