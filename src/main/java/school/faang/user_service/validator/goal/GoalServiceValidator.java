package school.faang.user_service.validator.goal;

import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalServiceValidator {

    void validateActiveGoalsLimit(Long userId);

    Goal existsById(Long goalId);

    void validateForUpdating(UpdateGoalDto goalDto);
}
