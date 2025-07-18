package school.faang.user_service.service.goal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

public interface GoalService {
    GoalDto create(CreateGoalDto createGoalDto);

    Page<GoalDto> get(FilterGoalDto dto, Pageable pageable);

    void delete(Long id);

    GoalDto update(Long id, UpdateGoalDto updateGoalDto);
}
