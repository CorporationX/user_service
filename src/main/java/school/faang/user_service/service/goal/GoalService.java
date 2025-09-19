package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

public interface GoalService {

    GoalDto create(CreateGoalDto createGoalDto);

    GoalDto update(long goalId, UpdateGoalDto updateGoalDto);

    void delete(long goalId);

    List<GoalDto> getByFilters(GoalFilterDto filters);
}
