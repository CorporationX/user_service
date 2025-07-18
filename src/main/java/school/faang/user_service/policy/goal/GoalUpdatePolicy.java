package school.faang.user_service.policy.goal;

import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalUpdatePolicy {
    void validate(UpdateGoalDto dto, Goal goal);
}
