package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filteredGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, filteredGoalDto);
    }

    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filterGoalDto) {
        return goalService.findGoalsByUserId(userId, filterGoalDto);
    }
}
