package school.faang.user_service.service.goal.filters;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.HashSet;
import java.util.List;

public class SkillIdsGoalFilter implements GoalFilter {
    private final List<Long> skillIds;

    public SkillIdsGoalFilter(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    @Override
    public List<GoalDto> applyFilter(List<GoalDto> goals) {
        return goals.stream()
                .filter(goal -> new HashSet<>(skillIds).containsAll(goal.getSkillIds()) && isApplicable(goal))
                .toList();
    }

    @Override
    public boolean isApplicable(GoalDto goalDto) {
        return goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty();
    }
}