package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.GoalValidator;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    @GetMapping("/users/{userId}/goals")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filters) {
        List<GoalDto> goals = goalService.getGoalsByUser(userId, filters);
        return new ResponseEntity<>(goals, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/goals")
    public ResponseEntity<GoalDto> createGoal(@PathVariable Long userId, @RequestBody GoalDto goal) {
        goalValidator.validateGoal(userId, goal);
        GoalDto createdGoal = goalService.createGoal(userId, goal);
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }
}