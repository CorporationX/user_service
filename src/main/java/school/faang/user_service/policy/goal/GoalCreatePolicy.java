package school.faang.user_service.policy.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;

public interface GoalCreatePolicy {
    void validate(CreateGoalDto dto);
}
