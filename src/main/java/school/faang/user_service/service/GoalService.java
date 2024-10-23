package school.faang.user_service.service;

import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.List;

public interface GoalService {

    GoalDto createGoal(long userId, GoalDto goalDto);

    GoalDto updateGoal(long goalId, GoalDto goalDto);

    void deleteGoal(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto);

    List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto);

    void removeGoals(List<Goal> goalIds);

    GoalNotificationDto getGoal(long goalId);
}
