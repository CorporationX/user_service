package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getGoalStatusFilter() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto goalFilterDto) {
        if (!isApplicable(goalFilterDto))
            return null;

        return goalStream.filter(goal -> goal.getStatus() == goalFilterDto.getGoalStatusFilter());
    }
}
