package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    public GoalDto createGoal(long userId, GoalDto goalDto);

    public GoalDto updateGoal(long goalId, GoalDto goalDto);

    public void deleteGoal(long goalId);

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto);

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto);

    void removeGoals(List<Goal> goalIds);
}
