package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, GoalDto goal) {
        validateGoalTitle(goal);
        goalService.createGoal(userId, goal);
    }

    private void validateGoalTitle(GoalDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Goal title is empty or null");
        }
    }
}
