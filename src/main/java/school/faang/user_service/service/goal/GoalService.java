package school.faang.user_service.service.goal;

import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;

import java.util.List;

public interface GoalService {
    GoalDto createGoal(Long userId, GoalDto goalDto);

    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    void deleteGoal(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, SearchGoalDto searchGoalDto);

    List<GoalDto> getGoalsByUser(long userId, SearchGoalDto searchGoalDto);
}
