package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goal")
@Tag(name = "Endpoint controller Goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create/{userId}")
    @Operation(summary = "Create goal in database")
    public GoalDto createGoal(@PathVariable("userId") Long userId, @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/update/{goalId}")
    @Operation(summary = "Update goal in database")
    public GoalDto updateGoal(@PathVariable("goalId") Long goalId, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/delete/{goalId}")
    @Operation(summary = "Delete goal from database")
    public void deleteGoal(@PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/subtask/{goalId}")
    @Operation(summary = "Get a list of subtasks by task id")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") Long goalId, @RequestBody GoalFilterDto filteredGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, filteredGoalDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get list goal by user id")
    public List<GoalDto> findGoalsByUserId(@PathVariable("userId") Long userId, @RequestBody GoalFilterDto filterGoalDto) {
        return goalService.findGoalsByUserId(userId, filterGoalDto);
    }
}
