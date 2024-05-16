package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final GoalValidator goalValidator;

    @PostMapping("/createGoal/{userId}")
    public GoalDto createGoal(@PathVariable Long userId, @RequestBody GoalDto goalDto) {
        System.out.println("userId = " + userId + ", goalDto = " + goalDto);
        goalValidator.validateGoalTitle(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/updateGoal/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        goalValidator.validateGoalTitle(goalDto);
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/deleteGoal/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/getSubtasksByGoal/{goalId}")
    public List<GoalDto> getSubtasksByGoal(@PathVariable Long goalId, @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @GetMapping("/getGoalsByUser/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable long userId, @RequestBody GoalFilterDto filter) {
        return goalService.getGoalsByUserId(userId, filter);
    }
}
