package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
@Tag(name = "Endpoint controller Goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create/{userId}")
    @Operation(summary = "Create goal in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the goal",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDto.class)) }) })
    public GoalDto createGoal(@PathVariable("userId") Long userId, @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/update/{goalId}")
    @Operation(summary = "Update goal in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the goal",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDto.class)) }) })
    public GoalDto updateGoal(@PathVariable("goalId") Long goalId, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/delete/{goalId}")
    @Operation(summary = "Delete goal from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the goal")})
    public void deleteGoal(@PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/subtask/{goalId}")
    @Operation(summary = "Get a list of subtasks by task id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting list subtasks",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDto.class, type = "array")) }) })
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") Long goalId, @RequestBody GoalFilterDto filteredGoalDto) {
        return goalService.findSubtasksByGoalId(goalId, filteredGoalDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get list goal by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting goals by user id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDto.class, type = "array")) }) })
    public List<GoalDto> findGoalsByUserId(@PathVariable("userId") Long userId, @RequestBody GoalFilterDto filterGoalDto) {
        return goalService.findGoalsByUserId(userId, filterGoalDto);
    }
}
