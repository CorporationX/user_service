package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Endpoints for managing goals")
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "Create user's goal by user id")
    @PostMapping("/goal")
    public GoalDto createGoal(@RequestHeader("userId") Long userId, @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @Operation(summary = "Update goal by goal id")
    @PutMapping("/goal/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @Operation(summary = "Delete goal by goal id")
    @DeleteMapping("/goal/{goalId}")
    public HttpStatus deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return HttpStatus.NO_CONTENT;
    }

    @Operation(summary = "Get goal subtasks by goal id and filters")
    @PostMapping("/goal/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @Operation(summary = "Get user's goals by user id and filters")
    @PostMapping("/goals")
    public List<GoalDto> getGoalsByUser(@RequestHeader("userId") Long userId, @RequestBody GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}