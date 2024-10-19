package school.faang.user_service.service;

import school.faang.user_service.model.dto.GoalDto;

public interface GoalService {
    GoalDto updateGoal(long goalId, GoalDto goalDto);

    GoalDto getGoal(long goalId);
}
