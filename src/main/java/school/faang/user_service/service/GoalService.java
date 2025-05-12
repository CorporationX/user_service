package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    GoalDto createGoal(Long userId, Goal goal);

    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    GoalDto deleteGoal(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter);

    List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter);

    Goal findById(Long id);
}
