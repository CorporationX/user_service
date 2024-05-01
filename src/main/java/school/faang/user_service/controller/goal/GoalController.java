package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Component
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void createGoal(Long userId, GoalDto goalDto) {

    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        validateGoalTitle(goalDto);
        return goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    private void validateGoalTitle(GoalDto goalDto) {
        if (goalDto.getTitle().isEmpty()) {
            throw new DataValidationException("Название цели не должно быть пустым");
        }
    }
}
