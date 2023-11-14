package school.faang.user_service.dto.goal.filter;

import java.util.stream.Stream;

import school.faang.user_service.entity.goal.Goal;

public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getStatusFilter() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getStatus().equals(filters.getStatusFilter()));
    }
}
