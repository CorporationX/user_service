package school.faang.user_service.controller.goal;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import school.faang.user_service.controller.goal.validation.GoalControllerValidator;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Tag(name = "GoalController", description = "Goal API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalControllerValidator goalControllerValidator;

    @Operation(summary = "Create Goal", description = "create new goal")
    @PostMapping(value = "{userId}/goals/create")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@Valid  @Parameter(description = "user id") @PathVariable Long userId, @RequestBody GoalDto goalDto) {
        goalControllerValidator.validateCreation(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    @Operation(summary = "Update Goal", description = "update goal by goal id")
    @PutMapping(value = "/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@Valid  @Parameter(description = "goal id") @PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        goalControllerValidator.validateUpdating(goalDto);
        return goalService.updateGoal(goalId, goalDto);
    }

    @Operation(summary = "Delete goal", description = "delete goal by goal id")
    @DeleteMapping(value = "/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGoal(@Valid  @Parameter(description = "goal id") @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @Operation(summary = "Find subtasks", description = "find subtasks by goal id")
    @GetMapping(value = "/goals/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findSubtaskByGoalId(@Valid  @Parameter(description = "goal id") @PathVariable Long goalId, @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @Operation(summary = "Find goals", description = "find goals by user id")
    @GetMapping(value = "/users/{userId}/goals")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findGoalsByUser(@Valid  @Parameter(description = "user id") @PathVariable Long userId, @RequestBody GoalFilterDto filters) {
        return goalService.findGoalsByUserId(userId, filters);
    }
}
