package school.faang.user_service.controller.goal;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.goal.GoalValidation;

import java.util.List;

@Data
@RestController
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final GoalValidation goalValidation;

    public void createGoal(Long userId, GoalDto goal) {
        goalValidation.validateTitleAndUserId(goal, userId);
        goalService.createGoal(userId, goal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goal) {
        goalValidation.validateTitleAndGoalId(goalId, goal);
        return goalService.updateGoal(goalId, goal);
    }

    public void deleteGoal(Long goalId) {
        goalValidation.validateGoalId(goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        goalValidation.validateUserIdAndFilter(userId, filter);
        return goalService.findGoalsByUser(userId, filter);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidation.validateGoalIdAndFilter(goalId, filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }
}
