package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;


    public GoalDto updateGoal(Long goalId, GoalDto goal) {
        goalValidator.validateUserId(goalId);
        goalValidator.validateGoalTitle(goal);
        return goalService.updateGoal(goalId, goal);
    }

    public GoalDto createGoal(Long userId, GoalDto goal) {
        goalValidator.validateUserId(userId);
        goalValidator.validateGoalTitle(goal);
        return goalService.createGoal(userId, goal);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }


    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidator.validateFilter(filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

}
