package school.faang.user_service.filters.goal;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filters.goal.dto.GoalFilterDto;

import java.util.stream.Stream;

public class GoalTitleFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getTitle() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goal.getTitle().contains(goalFilterDto.getTitle()));
    }
}