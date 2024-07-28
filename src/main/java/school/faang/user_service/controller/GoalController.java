package school.faang.user_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Tag(name = "GoalController", description = "Goal API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "Create Goal", description = "create new goal")
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@Parameter(description = "user id") @PathVariable Long userId,
                              @Validated @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @Operation(summary = "Update Goal", description = "update goal by goal id")
    @PutMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@Parameter(description = "goal id") @PathVariable Long goalId,
                              @Validated @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @Operation(summary = "Delete goal", description = "delete goal by goal id")
    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGoal(@Parameter(description = "goal id") @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @Operation(summary = "Find subtasks", description = "find subtasks by goal id")
    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findSubtaskByGoalId(@Parameter(description = "goal id") @PathVariable Long goalId,
                                              @Validated @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @Operation(summary = "Find goals", description = "find goals by user id")
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findGoalsByUser(@Parameter(description = "user id") @PathVariable Long userId,
                                          @Validated @RequestBody GoalFilterDto filters) {
        return goalService.findGoalsByUserId(userId, filters);
    }
}
