package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto filter) {
        goals.removeIf(g -> g.getStatus() != filter.getStatus());
    }

}
