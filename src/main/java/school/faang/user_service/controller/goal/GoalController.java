package school.faang.user_service.controller.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final ValidationGoal validationGoal;

    @PostMapping("/goal/{userId}")
    public GoalDto createGoal(@NonNull @PathVariable Long userId, @NonNull @RequestBody GoalDto goalDto) {
        validationGoal.checkGoalTitleForBlank(goalDto);

        return goalService.createGoal(userId, goalDto);
    }

    @DeleteMapping("/delete/{goalId}")
    public void deleteGoal(@NonNull @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PutMapping("/goal/{goalId}")
    public void updateGoal(@NonNull @PathVariable Long goalId, @NonNull GoalDto goalDto) {
        validationGoal.checkGoalTitleForBlank(goalDto);

        goalService.updateGoal(goalId, goalDto);
    }

    @PostMapping("/subtasks/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@NonNull @PathVariable Long goalId, @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @PostMapping("/goals/{userId}")
    public List<GoalDto> getGoalsByUser(@NonNull @PathVariable Long userId, @RequestBody GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}
