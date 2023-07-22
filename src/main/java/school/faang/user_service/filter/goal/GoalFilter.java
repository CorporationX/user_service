package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filterDto);

    void apply(List<GoalDto> list, GoalFilterDto filterDto);
}
