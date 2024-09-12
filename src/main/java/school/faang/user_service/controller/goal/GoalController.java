package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/v1/goals")
@AllArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userID}")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@PathVariable("userID") @NonNull Long userId,
                              @RequestBody @Valid GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@PathVariable("goalId") @Positive long goalId,
                              @RequestBody @Valid GoalDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable("goalId") @Positive long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/filter/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> getSubtasksByGoalId(@PathVariable("goalId") @Positive long goalId,
                                             @RequestBody GoalFilterDto filter) {
        return goalService.getSubtasksByGoalId(goalId, filter);
    }

    @GetMapping("/filter/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> getGoalsByUser(@PathVariable("userId") @Positive long userId,
                                        @RequestBody GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}