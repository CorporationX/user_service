package school.faang.user_service.dto.goal.filter;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalTitleFilter implements  GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getTitleFilter() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getTitle().equals(filters.getTitleFilter()));
    }
}
