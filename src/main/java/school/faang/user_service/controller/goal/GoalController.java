package school.faang.user_service.controller.goal;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    @Autowired
    private final GoalService goalService;

    @GetMapping("/create/{userId}")
    public void createGoal(Long userId, Goal goal) {
        goalService.createGoal(userId, goal);
    }

    @GetMapping("/filters/{userId}")
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        if (userId == null) {
            throw new IllegalArgumentException("No userId!");
        }
        return goalService.getGoalsByUser(userId, filters);
    }

    @GetMapping("/filter-subtasks/{goalId}")
    public void findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        goalService.getSubtasksByGoalId(goalId, filters);
    }

    @GetMapping("/delete/{goalId}")
    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }
}