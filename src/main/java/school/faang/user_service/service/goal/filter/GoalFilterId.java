package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Service
public class GoalFilterId implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getId() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto goalFilterDto) {
        goals.removeIf(goal -> !goal.getId().equals(goalFilterDto.getId()));
    }
}
