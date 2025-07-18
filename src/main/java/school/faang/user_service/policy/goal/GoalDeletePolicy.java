package school.faang.user_service.policy.goal;

import school.faang.user_service.entity.goal.Goal;

public interface GoalDeletePolicy {
    void validate(Goal goal);
}
