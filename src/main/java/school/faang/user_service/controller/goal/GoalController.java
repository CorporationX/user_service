package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.EmptyGoalsException;
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

    @GetMapping("/users/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходимо указать идентификатор пользователя.");
        }

        if (filter == null) {
            throw new IllegalArgumentException("Необходимо указать фильтр целей.");
        }

        List<GoalDto> goals = goalService.getGoalsByUser(userId, filter);

        if (goals.isEmpty()) {
            throw new EmptyGoalsException("Список целей пользователя пуст.");
        }

        return goals;
    }
}