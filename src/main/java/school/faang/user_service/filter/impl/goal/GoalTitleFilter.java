package school.faang.user_service.filter.impl.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.GoalFilter;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@Component
public class GoalTitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitle() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto filter) {
        goals.removeIf(g -> !g.getTitle().contains(filter.getTitle()));
    }
}
