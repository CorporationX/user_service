package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalCreateBeforeFilter implements GoalFilter {

    @Override
    public boolean doFilter(Goal goal, GoalFilterDto criteria) {
        return goal.getCreatedAt().isBefore(criteria.getCreatedBefore());
    }

    @Override
    public boolean isApplicable(GoalFilterDto criteria) {
        return criteria.getCreatedBefore() != null;
    }
}
