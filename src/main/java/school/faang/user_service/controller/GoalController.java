package school.faang.user_service.controller;

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
public class GoalController {

    private final GoalServiceImpl goalService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping("/{id}")
    public ResponseEntity<CreateGoalResponseDto> create(@PathVariable(name = "id") Long userId,
                                                        @RequestBody CreateGoalRequestDto request) {
        return ResponseEntity.ok(goalService.createGoal(userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.ok("Goal successfully deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<GoalDto>> findAllSubtasks(@PathVariable(name = "id") Long parentGoalId) {
        return ResponseEntity.ok(goalService.findSubtasksByGoalId(parentGoalId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<GoalDto>> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(goalService.search(request));
    }

}
