package school.faang.user_service.controller.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping(value = "/users/{userId}/goals")
    public GoalDto createGoal(@NonNull @PathVariable("userId") Long userId, @NonNull @RequestBody GoalDto goal) {
        return goalService.createGoal(userId, goal);
    }

    @PutMapping(value = "/goals/{goalId}")
    public GoalDto updateGoal(@NonNull @PathVariable("goalId") Long goalId, @NonNull @RequestBody GoalDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping(value = "/goals/{goalId}")
    public void deleteGoal(@NonNull @PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping(value = "/goals/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@NonNull @PathVariable("goalId") Long goalId) {
        return goalService.findSubtasksByGoalId(goalId);
    }

    @GetMapping(value = "/users/{userId}/goals")
    public List<GoalDto> getGoalsByUser(@NonNull @PathVariable("userId") Long userId, @NonNull GoalFilterDto filter) {
        return goalService.getGoalsByUserId(userId, filter);
    }
}
