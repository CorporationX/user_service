package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalIdDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BusinessException;

public interface GoalService {
    Goal getGoalById(Long goalId);

    GoalIdDto createGoal(GoalCreateDto goalCreateRq) throws BusinessException;

    GoalIdDto deleteGoal(Long goalId);

    GoalIdDto updateGoal(Long goalId, GoalDto goalUpdateRq);
}
