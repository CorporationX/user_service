package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitle() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getTitle().contains(filters.getTitle()));
    }
}
