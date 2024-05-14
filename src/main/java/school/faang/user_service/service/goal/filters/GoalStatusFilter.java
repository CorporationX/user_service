package school.faang.user_service.service.goal.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return !filters.getStatusPattern().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getStatus().toString().equals(filters.getStatusPattern()));
    }
}
