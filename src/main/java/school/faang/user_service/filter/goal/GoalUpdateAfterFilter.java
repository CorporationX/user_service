package school.faang.user_service.filter.goal;

import lombok.Setter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalUpdateAfterFilter implements GoalFilter {
    @Setter
    private GoalFilterDto criteria;

    @Override
    public boolean doFilter(Goal goal) {
        return goal.getUpdatedAt().isAfter(criteria.getUpdatedAfter());
    }

    @Override
    public boolean isApplicable() {
        return criteria.getUpdatedAfter() != null;
    }
}
