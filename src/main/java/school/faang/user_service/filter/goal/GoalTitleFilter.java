package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getTitle().contains(filter.getTitle()));
    }
}

