package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public void createGoal(@Min(value = 0, message = "UserID must be at least 0") long userId,
                           @Validated GoalDto goalDto) {
        goalValidator.validateUserId(userId);
        goalService.createGoal(userId, goalDto);
    }

    public void update(@Min(value = 0, message = "GoalID must be at least 0") long goalId,
                       @Validated GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(@Min(value = 0, message = "GoalID must be at least 0") long goalId) {
        goalValidator.validateGoalId(goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(@Min(value = 0, message = "GoalID must be at least 0") long goalId,
                                              GoalFilterDto filterDto) {
        goalValidator.validateGoalId(goalId);
        return goalService.findSubtasksByGoalId(goalId, filterDto);
    }

    public List<GoalDto> getGoalsByUser(@Min(value = 0, message = "UserID must be at least 0") long userId,
                                        GoalFilterDto filterDto) {
        goalValidator.validateUserId(userId);
        return goalService.getGoalsByUser(userId, filterDto);
    }
}
