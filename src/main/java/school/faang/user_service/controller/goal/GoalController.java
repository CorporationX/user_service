package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping("/create")
    public ResponseEntity<String> createGoal(@RequestParam Long userId, @RequestBody Goal goal) {
        goalService.createGoal(userId, goal);
        return ResponseEntity.ok("Goal created successfully");
    }

    @PutMapping("/update/{goalId}")
    public ResponseEntity<String> updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        goalService.updateGoal(goalId, goal);
        return ResponseEntity.ok("Goal updated successfully");
    }

    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    @GetMapping("/subtasks/{goalId}")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(@PathVariable long goalId, @RequestParam(required = false) GoalFilterDto filter) {
        List<GoalDto> subtasks = goalService.findSubtasksByGoalIdWithFilter(goalId, filter);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/goals")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(
            @RequestParam Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) RequestStatus status) {

        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(title);
        filter.setStatus(status);

        List<GoalDto> goals = goalService.findGoalsByUserIdWithFilter(userId, filter);
        return ResponseEntity.ok(goals);
    }
}