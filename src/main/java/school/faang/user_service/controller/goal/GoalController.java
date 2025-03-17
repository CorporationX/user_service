package school.faang.user_service.controller.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping()
    public ResponseEntity<GoalDto> createGoal(@RequestParam Long userId,
                                              @RequestBody @NonNull GoalDto goal) {
        GoalDto createdGoal = goalService.createGoal(userId, goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(@PathVariable("goalId") Long goalId,
                                              @RequestBody @NonNull GoalDto goal) {
        GoalDto updatedGoal = goalService.updateGoal(goalId, goal);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-subtasks/{goalId}")
    public ResponseEntity<List<GoalDto>>
    findSubtasksByGoalId(@PathVariable("goalId") long goalId, @NonNull SearchGoalDto searchGoal) {
        List<GoalDto> subTasks = goalService.findSubtasksByGoalId(goalId, searchGoal);
        return ResponseEntity.ok(subTasks);
    }

    @GetMapping("/find-goals-by-user/{userId}")
    public ResponseEntity<List<GoalDto>>
    getGoalsByUser(@PathVariable("userId") long userId, @NonNull SearchGoalDto searchGoal) {
        List<GoalDto> goalsByUser = goalService.getGoalsByUser(userId, searchGoal);
        return ResponseEntity.ok(goalsByUser);
    }

    @ExceptionHandler(GoalDataException.class)
    public ResponseEntity<String> handleGoalDataException(GoalDataException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
