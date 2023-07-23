package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @GetMapping("/get/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable("userId") Long userId,
                                        @RequestBody(required = false) GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }

    @GetMapping("/get/subtasks/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") Long goalId,
                                              @RequestBody(required = false) GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/user/{userId}/create-goal")
    public GoalDto createGoal(@PathVariable("userId") Long userId,
                              @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/update/{goalId}")
    public GoalDto updateGoal(@PathVariable("goalId") Long goalId,
                              @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/delete/{goalId}")
    public boolean deleteGoal(@PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);
        return true;
    }
}
