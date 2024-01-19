package school.faang.user_service.filter.impl.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

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
    public List<Goal> apply(List<Goal> goals, GoalFilterDto filter) {
        return goals.stream().filter(g -> g.getTitle().contains(filter.getTitle())).toList();
    }
}
