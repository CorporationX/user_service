package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public void deleteGoal(Long goalId) {
        if (goalId < 1) {
            throw new DataValidationException("If cannot be less than 1");
        }
        service.deleteGoal(goalId);
    }
}
