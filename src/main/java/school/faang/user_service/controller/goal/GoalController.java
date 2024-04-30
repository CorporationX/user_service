package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@Component
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        titleValidation(goal);

        goalService.createGoal(userId, goal);
    }

    public void updateGoal(Long goalId, GoalDto goal) {

    }

    private void titleValidation(Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal title can't be empty");
        }
    }
}
