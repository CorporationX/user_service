package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component("goalStatusFilter")
public class StatusFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getStatus().equals(filters.getStatus()));
    }
}
