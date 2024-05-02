package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto goalFilterDto);
    GoalDto createGoal(Long userId, Goal goal);
    GoalDto updateGoal(Long goalId, GoalDto goalDto);
    void deleteGoal(long goalId);
    List<GoalDto> findSubtasksByGoalId(long goalId);
}

