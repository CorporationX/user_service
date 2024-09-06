package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    void createGoal(Goal goal, Long userId);

    void updateGoal(Goal goal);

    void deleteGoal(Long goalId);

    List<Goal> findSubGoalsByParentGoalId(Long parentGoalId, GoalFilterDto filterDto);

    List<Goal> findGoalsByUserId(Long userId, GoalFilterDto filter);
}
