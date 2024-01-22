package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public void createGoal(Long userId, GoalDto goal) {
        if (goalValidator.isValidateByEmptyTitle(goal)) {
            goalService.createGoal(userId, goal);
        }
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        if (goalValidator.isValidateByEmptyTitle(goal)) {
            goalService.updateGoal(goalId, goal);
        }
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        goalValidator.validateUserId(userId);
        return goalService.findGoalsByUser(userId, filter);
    }
}
