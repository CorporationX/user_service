package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/v1/goals")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@PathVariable("userId") @Positive Long userId,
                              @RequestBody @Validated(GoalDto.Before.class) GoalDto goalDto) {
        log.info("Creating goal for user with ID: {}", userId);
        return goalService.createGoal(userId, goalDto);
    }

    @PatchMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@PathVariable("goalId") @Positive Long goalId,
                              @RequestBody @Validated(GoalDto.After.class) GoalDto goal) {
        log.info("Updating goal with ID: {}", goalId);
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable("goalId") @Positive Long goalId) {
        log.info("Deleting goal with ID: {}", goalId);
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") @Positive Long goalId,
                                              GoalFilterDto filterDto) {
        log.info("Finding subtasks for goal ID: {} with filter: {}", goalId, filterDto);
        return goalService.findSubtasksByGoalId(goalId, filterDto);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> findGoalsByUserId(@PathVariable("userId") @Positive Long userId,
                                           GoalFilterDto filterDto) {
        log.info("Finding goals for user ID: {} with filter: {}", userId, filterDto);
        return goalService.findGoalsByUserId(userId, filterDto);
    }
}
