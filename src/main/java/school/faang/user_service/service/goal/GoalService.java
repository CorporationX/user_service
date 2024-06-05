package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    List<GoalDto> getGoalsByUser(long userId, GoalFilterDto goalFilterDto);

    GoalDto createGoal(Long userId, GoalDto goal);

    GoalDto updateGoal(long userId, Long goalId, GoalDto goalDto);

    GoalDto deleteGoal(long goalId);

    List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filter);

    Goal findGoalById(long id);

    int findActiveGoalsByUserId(long id);

    void delete(Goal goal);
}

