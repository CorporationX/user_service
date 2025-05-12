package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto createGoal(Long userId, Goal goal) {
        if (goal.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        return goalService.createGoal(userId, goal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        return goalService.updateGoal(goalId, goalDto);
    }

    public GoalDto deleteGoal(long goalId) {
        return goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        return goalService.findSubtasksByGoalId(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }
}
