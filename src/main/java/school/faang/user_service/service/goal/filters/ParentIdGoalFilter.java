package school.faang.user_service.service.goal.filters;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.List;

public class ParentIdGoalFilter implements GoalFilter {
    private final Long parentId;

    public ParentIdGoalFilter(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public List<GoalDto> applyFilter(List<GoalDto> goals) {
        return goals.stream()
                .filter(goal -> parentId.equals(goal.getParentId()) && isApplicable(goal))
                .toList();
    }

    @Override
    public boolean isApplicable(GoalDto goalDto) {
        return goalDto.getParentId() != null;
    }
}