package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getGoalStatus() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto goalFilterDto) {
        goals.removeIf(goal -> !goal.getStatus().equals(goalFilterDto.getGoalStatus()));
    }
}
