package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

public interface GoalService {

    GoalDto create(Long userId, GoalDto goalDto);

    GoalDto update(UpdateGoalDto updateGoalDto);

    void delete(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, String title, String status);

    List<GoalDto> findGoalsByUserIdAndFilter(long userId, String title, String status);
}
