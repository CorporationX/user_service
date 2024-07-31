package school.faang.user_service.controller.goal;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userID}")
    public ResponseEntity<GoalDto> createGoal(@PathVariable("userID") @NonNull Long userId,
                                              @RequestBody @Valid GoalDto goalDto) {
        GoalDto result = goalService.createGoal(userId, goalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(@PathVariable("goalId") @Positive long goalId,
                                              @RequestBody @Valid GoalDto goal) {
        GoalDto result = goalService.updateGoal(goalId, goal);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") @Positive long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/{goalId}")
    public ResponseEntity<List<GoalDto>> getSubtasksByGoalId(@PathVariable("goalId") @Positive long goalId,
                                                             @RequestBody GoalFilterDto filterGoals) {
        List<GoalDto> result = goalService.getSubtasksByGoalId(goalId, filterGoals);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(@PathVariable("userId") @Positive long userId,
                                                        @RequestBody GoalFilterDto filterGoals) {
        List<GoalDto> result = goalService.getGoalsByUser(userId, filterGoals);
        return ResponseEntity.ok(result);
    }
}
