package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Tag(name = "Goal", description = "Api for goals management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "create new goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "goal created successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data"),
            @ApiResponse(responseCode = "404", description = "goal or user with received id not found")})
    @PostMapping
    public GoalDTO createGoal(@RequestParam Long userId, @RequestBody GoalDTO goalDTO) {
        if (goalDTO.getTitle() == null || goalDTO.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be empty");
        }
        return goalService.createGoal(userId, goalDTO);
    }

    @Operation(summary = "change goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "goal changed successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data"),
            @ApiResponse(responseCode = "404", description = "goal with received id not found")})
    @PutMapping("/{goalId}")
    public GoalDTO updateGoal(@PathVariable Long goalId, @RequestBody GoalDTO goalDTO) {
        if (goalDTO.getTitle() == null || goalDTO.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be empty");
        }
        return goalService.updateGoal(goalId, goalDTO);
    }

    @Operation(summary = "get goal by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "goal received successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data")})
    @GetMapping("user/{userId}")
    public List<GoalDTO> getGoalsByUser(@PathVariable Long userId,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String status) {
        GoalFilterDTO goalFilterDTO = new GoalFilterDTO(title, status);
        return goalService.getGoalsByUser(userId, goalFilterDTO);
    }

    @Operation(summary = "get goals by parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sub goals received successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data")})
    @GetMapping("sub-goals/{parentId}")
    public List<GoalDTO> findSubGoals(@PathVariable Long parentId,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) String status) {
        GoalFilterDTO goalFilterDTO = new GoalFilterDTO(title, status);
        return goalService.getSubGoals(parentId, goalFilterDTO);
    }

    @Operation(summary = "delete goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "goal deleted successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data"),
            @ApiResponse(responseCode = "404", description = "goal with received id not found")})
    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
    }
}
