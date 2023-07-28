package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;
    private final GoalValidator validator;

    public GoalDto createGoal(long userId, GoalDto goalDto) {
        validator.createGoalControllerValidation(userId, goalDto);
        return service.createGoal(userId, goalDto);
    }
}
