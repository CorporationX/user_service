package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<Goal> filter(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goals.filter(goal -> goal.getStatus().equals(filterDto.getStatus()));
    }
}
