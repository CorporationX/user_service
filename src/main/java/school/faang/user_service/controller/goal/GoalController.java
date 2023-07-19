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

    public void createGoal(Long userId, Goal goal) throws DataValidationException {
        if (goal == null) {
            throw new DataValidationException("Goal cannot be null");
        }
        if (goal.getTitle() == null || goal.getTitle().isBlank())
            throw new DataValidationException("Title can not be blank or null");
        service.createGoal(userId, goal);
    }
}
