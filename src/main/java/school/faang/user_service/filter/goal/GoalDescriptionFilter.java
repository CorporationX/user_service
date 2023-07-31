package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.dto.GoalFilterDto;

import java.util.stream.Stream;

public class GoalDescriptionFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getDescription() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goal.getDescription().contains(goalFilterDto.getDescription()));
    }
}