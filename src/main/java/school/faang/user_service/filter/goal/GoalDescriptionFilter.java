package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
class GoalDescriptionFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getDescription().contains(filter.getDescription()));
    }
}
