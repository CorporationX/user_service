package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;


    public GoalDto createGoal(Long userId, GoalDto goal) {
        goalValidator.validateUserId(userId);
        goalValidator.validateGoalTitle(goal);
        return goalService.createGoal(userId, goal);
    }
}
