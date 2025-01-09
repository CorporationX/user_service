package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

public interface GoalService {

    GoalDto create(Long userId, GoalDto goalDto);

    GoalDto update(UpdateGoalDto updateGoalDto);
}
