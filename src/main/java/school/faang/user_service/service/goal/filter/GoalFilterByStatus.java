package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.Objects;

@Component
public class GoalFilterByStatus implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return Objects.nonNull(filters.getStatus());
    }

    @Override
    public boolean test(Goal goal, GoalFilterDto filterDto) {
        GoalStatus requiredStatus = filterDto.getStatus();
        return goal.getStatus().equals(requiredStatus);
    }
}
