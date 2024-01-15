package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void deleteGoal(Long goalId) {
        if (goalId == null || goalId < 0) {
            throw new IllegalArgumentException("Invalid ID: " + goalId);
        } else {
            goalService.deleteGoal(goalId);
        }
    }
}