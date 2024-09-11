package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public void createGoal(@Positive long userId, @Validated GoalDto goalDto) {
        goalValidator.validateUserId(userId);
        goalService.createGoal(userId, goalDto);
    }

    public void update(@Positive long goalId, @Validated GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(@Positive long goalId) {
        goalValidator.validateGoalId(goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(@Positive long goalId, GoalFilterDto filterDto) {
        goalValidator.validateGoalId(goalId);
        return goalService.findSubtasksByGoalId(goalId, filterDto);
    }

    public List<GoalDto> getGoalsByUser(@Positive long userId, GoalFilterDto filterDto) {
        goalValidator.validateUserId(userId);
        return goalService.getGoalsByUser(userId, filterDto);
    }
}
