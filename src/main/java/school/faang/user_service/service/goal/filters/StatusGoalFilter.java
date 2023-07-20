package school.faang.user_service.service.goal.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.stream.Stream;

@Component
public class StatusGoalFilter implements GoalFilter {
    public Stream<Goal> applyFilter(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getStatus() == filters.getStatus());
    }

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getStatus() != null;
    }
}
