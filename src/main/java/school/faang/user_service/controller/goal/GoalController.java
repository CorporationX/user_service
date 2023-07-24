package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        return goalService.getGoalsByUser(userId, filterDto);
    }

    public List<GoalDto> getSubGoalsByFilter(Long parentId, GoalFilterDto filterDto) {
        return goalService.getSubGoalsByFilter(parentId, filterDto);
    }
}
