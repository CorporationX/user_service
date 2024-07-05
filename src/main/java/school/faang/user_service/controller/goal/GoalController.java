package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@RestController
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("")
    public ResponseEntity<Goal> createGoal(Long userId, Goal goal) {
        Goal createdGoal = goalService.createGoal(userId, goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    @PostMapping("")
    public ResponseEntity<Goal> updateGoal(Long goalId, GoalDto goalDto) {
        Goal updatedGoal = goalService.updateGoal(goalId, goalDto);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
}

