package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

public interface GoalService {
    void createGoal(GoalDto goalDto);

    void updateGoal(GoalDto goalDto);

    void deleteGoal(Long goalId);

    List<GoalDto> findSubGoalsByParentGoalId(Long parentGoalId, GoalFilterDto filterDto);

    List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter);
}
