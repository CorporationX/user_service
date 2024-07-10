package school.faang.user_service.controller.goal;


import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.validator.GoalControllerValidate;

import java.util.List;


@Controller
@RequiredArgsConstructor
@Validated
public class GoalController {
    private final GoalService goalService;
    private final GoalControllerValidate validate;

    public void createGoal(@NonNull Long userId, @Valid Goal goal) {
        goalService.createGoal(userId, goal);
    }

    public void updateGoal(long goalId, GoalDto goal) {
        validate.validateId(goalId);
        goalService.updateGoal(goalId, goal);
    }

    public void deleteGoal(long goalId) {
        validate.validateId(goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        validate.validateId(goalId);
        return goalService.getSubtasksByGoalId(goalId, filters);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filters) {
        validate.validateId(userId);
        return goalService.getGoalsByUser(userId, filters);
    }
}
