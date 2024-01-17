package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

/**
 * @author Ilia Chuvatkin
 */

@Component
public class GoalTitleFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitle() != null;
    }

    @Override
    public void apply(Stream<Goal> goals, GoalFilterDto filter) {
        goals.filter(g -> g.getTitle().contains(filter.getTitle()));
    }
}
