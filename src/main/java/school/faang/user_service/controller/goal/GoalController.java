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
        return service.getGoalsByUser(userId, filter);
    }
}
