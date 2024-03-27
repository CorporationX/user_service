package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Endpoints for managing goals")
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserContext userContext;

    @Operation(summary = "Create a goal")
    @PostMapping
    public GoalDto createGoal(@Valid @RequestBody GoalDto goalDto) {
        long userId = userContext.getUserId();
        return goalService.createGoal(userId, goalDto);
    }

    @Operation(summary = "Get filtered user's goals")
    @PostMapping("/filtered")
    public List<GoalDto> getGoalsByUser(@RequestBody GoalFilterDto filters) {
        long userId = userContext.getUserId();
        return goalService.getGoalsByUser(userId, filters);
    }

    @Operation(summary = "Get goal subtasks by goal id and filters")
    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable @Positive(message = "ID can't be less than 1") Long goalId,
                                              @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @Operation(summary = "Update goal by goal id")
    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable @Positive(message = "ID can't be less than 1") Long goalId,
                              @Valid @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @Operation(summary = "Delete goal by goal id")
    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable @Positive(message = "ID can't be less than 1") Long goalId) {
        goalService.deleteGoal(goalId);
    }
}