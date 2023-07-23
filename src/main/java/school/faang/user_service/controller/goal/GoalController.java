package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public void updateGoal(Long goalId, GoalDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank())
            throw new DataValidationException("Title can not be blank or null");
        service.updateGoal(goalId, goal);
    }
}
