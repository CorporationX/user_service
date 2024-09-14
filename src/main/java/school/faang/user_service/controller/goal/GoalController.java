package school.faang.user_service.controller.goal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goal")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping
    public ResponseEntity<String> createGoal(@RequestParam Long userId, @RequestBody GoalDto goalDto) {
        goalService.createGoal(userId, goalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Цель успешно создана");
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<String> updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
        return ResponseEntity.ok("Цель успешно обновлена");
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.ok("Цель успешно удалена");
    }

    @GetMapping("/{goalId}/subtasks")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(
            @PathVariable long goalId,
            @RequestParam(required = false) String titleFilter) {
        List<GoalDto> subtasks = goalService.findSubtasksByGoalId(goalId, titleFilter);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/{userId}/goals")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) GoalStatus status,
            @RequestParam(required = false) Long skillId) {

        GoalFilterDto filter = new GoalFilterDto(title, status, skillId);

        List<GoalDto> goals = goalService.findGoalsByUserId(userId, filter);
        return ResponseEntity.ok(goals);
    }
}