package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        if (goal == null) {
            throwNullPointerExceptionWithMessage();
        }
        if (goal.getTitle().isBlank()) {
            throwDataValidationExceptionWithMessage();
        }
        goalService.createGoal(userId, goal);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        if (goalDto == null) {
            throwNullPointerExceptionWithMessage();
        }
        if (goalDto.getTitle().isBlank()) {
            throwDataValidationExceptionWithMessage();
        }
        goalService.updateGoal(goalId, goalDto);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }

    private void throwDataValidationExceptionWithMessage() {
        throw new DataValidationException("Goals title must exists");
    }

    private void throwNullPointerExceptionWithMessage() {
        throw new NullPointerException("Goal must exist");
    }
}
