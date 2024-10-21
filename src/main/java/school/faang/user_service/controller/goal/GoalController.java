package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userId}")
    public GoalDto createGoal(@PathVariable Long userId,@RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}
