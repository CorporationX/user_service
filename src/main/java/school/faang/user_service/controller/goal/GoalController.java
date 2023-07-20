package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/goal/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        if (goal.getTitle() != null && !goal.getTitle().isBlank()) {
            return goalService.updateGoal(goalId, goal);
        }
        throw new IllegalArgumentException("Invalid goal provided");
    }
}