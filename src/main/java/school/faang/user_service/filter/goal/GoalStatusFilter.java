package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;
import java.util.Objects;

@Component
public class GoalStatusFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return Objects.nonNull(filter.getStatus());
    }

    @Override
    public void apply(List<GoalDto> goals, GoalFilterDto filter) {
        goals.removeIf(goal -> !goal.getStatus().equals(filter.getStatus()));
    }
}
