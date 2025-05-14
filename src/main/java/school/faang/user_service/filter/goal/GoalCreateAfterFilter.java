package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalCreateAfterFilter implements GoalFilter {

    @Override
    public boolean doFilter(Goal goal, GoalFilterDto criteria) {
        return goal.getCreatedAt().isAfter(criteria.getCreatedAfter());
    }

    @Override
    public boolean isApplicable(GoalFilterDto criteria) {
        return criteria.getCreatedAfter() != null;
    }

}
