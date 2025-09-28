package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;

public interface GoalService {

    GoalDto completeGoal(Long goalId, Long userId);
}
