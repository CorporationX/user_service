package school.faang.user_service.service.goal.filters;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.List;

public class StatusGoalFilter implements GoalFilter {
    private final GoalStatus status;

    public StatusGoalFilter(GoalStatus status) {
        this.status = status;
    }

    @Override
    public List<GoalDto> applyFilter(List<GoalDto> goals) {
        return goals.stream()
                .filter(goal -> status.equals(goal.getStatus()) && isApplicable(goal))
                .toList();
    }

    @Override
    public boolean isApplicable(GoalDto goalDto) {
        return goalDto.getStatus() != null;
    }
}
