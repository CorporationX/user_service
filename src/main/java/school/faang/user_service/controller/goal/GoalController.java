package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/users/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filters) {

        return goalService.getGoalsByUser(userId, filters);
    }

    @PostMapping("/users/{userId}/goal")
    public ResponseEntity<String> createGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        try {
            goalService.createGoal(userId, goal);
            return ResponseEntity.ok("Goal created successfully");
        } catch (IllegalArgumentException e) {
            String errorMessage = "Invalid data";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);

        }
    }
}