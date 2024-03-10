package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/goal")
@Validated
public class GoalController {

    private final GoalService goalService;

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable @Positive(message = "Id должен быть положительным и больше 0") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable @Positive(message = "Id должен быть положительным и больше 0") Long userId,
                                        @RequestBody @Validated GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable @Positive(message = "Id должен быть положительным и больше 0") Long goalId) {
        return goalService.findSubtasksByGoalId(goalId);
    }

    @PostMapping("/{goalId}/subtasks/filter")
    public List<GoalDto> retrieveFilteredSubtasksForGoal(@PathVariable @Positive(message = "Id должен быть положительным и больше 0") Long goalId,
                                                         @RequestBody @Validated GoalFilterDto goalFilterDto) {
        return goalService.retrieveFilteredSubtasksForGoal(goalId, goalFilterDto);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable @Positive(message = "Id должен быть положительным и больше 0") Long goalId,
                              @RequestBody @Validated GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }
}