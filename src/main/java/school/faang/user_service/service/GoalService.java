package school.faang.user_service.service;

import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    public GoalDto createGoal(long userId, GoalDto goalDto);

    public GoalDto updateGoal(long goalId, GoalDto goalDto);

    public void deleteGoal(long goalId);

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto);

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto);

    void removeGoals(List<Goal> goalIds);
}
