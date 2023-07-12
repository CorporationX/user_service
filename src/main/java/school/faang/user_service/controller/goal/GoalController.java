package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public List<Goal> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (userId == null) throw new IllegalArgumentException("userId can not be Null");
        if (userId < 1) throw new IllegalArgumentException("userId can not be less than 1");
        return service.getGoalsByUser(userId, filter);
    }

    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) throw new IllegalArgumentException("Title can not be blank or null");
        service.createGoal(userId, goal);
    }
}
