package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController()
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{userId}")
    public ResponseEntity<GoalDto> createGoal(@PathVariable Long userId, @RequestBody GoalDto goalDto) {
        return new ResponseEntity<>(goalService.createGoal(userId, goalDto), HttpStatus.CREATED);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<HttpStatus> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @PostMapping("/{userId}/goals")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}