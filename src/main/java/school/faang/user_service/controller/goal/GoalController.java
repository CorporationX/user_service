package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.Optional;

@Component
public class GoalController {
    private final GoalService goalService;
    private final GoalRepository goalRepository;

    @Autowired
    public GoalController(GoalService goalService, GoalRepository goalRepository) {
        this.goalService = goalService;
        this.goalRepository = goalRepository;
    }

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }
}
