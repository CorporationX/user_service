package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;

@Component
public class StatusFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getStatus() != null;
    }

    @Override
    public boolean apply(GoalFilterDto goalFilterDto, Goal goal) {
        return Objects.equals(goal.getStatus(), goalFilterDto.getStatus());
    }
}
