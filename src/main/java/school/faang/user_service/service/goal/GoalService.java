package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    void deleteGoal(long goalID);

    void createGoal(long userId, Goal goal);

    List<GoalDto> findSubtasksByGoalId(long goalId);

    List<GoalDto> retrieveFilteredSubtasksForGoal(long goalId, GoalFilterDto goalFilterDto);

    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    List<GoalDto> getGoalsByUser(long userId, GoalFilterDto goalFilterDto);
}
