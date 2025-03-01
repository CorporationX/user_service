package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.request.CreateGoalRequestDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;
import school.faang.user_service.service.impl.GoalServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@Tag(name = "Goals", description = "Setting goals")
public class GoalController {

    private final GoalServiceImpl goalService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Allows you to check health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping("/{id}")
    @Operation(summary = "Create a goal", description = "Allows you to create a new goal")
    public ResponseEntity<CreateGoalResponseDto> create(@PathVariable(name = "id") Long userId,
                                                        @RequestBody CreateGoalRequestDto request) {
        return ResponseEntity.ok(goalService.createGoal(userId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal", description = "Allows you to delete a goal")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.ok("Goal successfully deleted");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieving all subtasks of a goal by filter", description = "Allows you to get all subtasks of a goal by filter")
    public ResponseEntity<List<GoalDto>> findAllSubtasks(@PathVariable(name = "id") Long parentGoalId) {
        return ResponseEntity.ok(goalService.findSubtasksByGoalId(parentGoalId));
    }

    @PostMapping("/search")
    @Operation(summary = "Getting a list of goals by filters", description = "Allows you to get a list of goals by filters")
    public ResponseEntity<List<GoalDto>> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(goalService.search(request));
    }

}
