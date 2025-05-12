package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {
    boolean doFilter(Goal goal);

    boolean isApplicable();

    void setCriteria(GoalFilterDto criteria);

}
