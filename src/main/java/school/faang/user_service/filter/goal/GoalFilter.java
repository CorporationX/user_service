package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {
    boolean doFilter(Goal goal, GoalFilterDto filterDto);

    boolean isApplicable(GoalFilterDto criteria);

}
