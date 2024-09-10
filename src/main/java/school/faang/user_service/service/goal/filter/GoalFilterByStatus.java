package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.Objects;

@Component
public class GoalFilterByStatus implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return Objects.nonNull(filters.getStatus());
    }

    @Override
    public List<Goal> applyFilter(List<Goal> goals, GoalFilterDto filtersParams) {
        GoalStatus requiredStatus = filtersParams.getStatus();
        return goals.stream()
                .filter(goal -> goal.getStatus().equals(requiredStatus))
                .toList();
    }
}
