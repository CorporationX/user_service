package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

/**
 * @author Ilia Chuvatkin
 */

public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(Stream<Goal> goals, GoalFilterDto filter) {
        goals.filter(g -> g.getStatus() == filter.getStatus());
    }
}
