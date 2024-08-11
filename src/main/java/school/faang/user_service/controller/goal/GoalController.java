package school.faang.user_service.controller.goal;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal")
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
                                             @RequestBody GoalFilterDto filterGoals) {
        return goalService.getSubtasksByGoalId(goalId, filterGoals);
    }

    @GetMapping("/filter/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> getGoalsByUser(@PathVariable("userId") @Positive long userId,
                                        @RequestBody GoalFilterDto filterGoals) {
        return goalService.getGoalsByUser(userId, filterGoals);
    }
}