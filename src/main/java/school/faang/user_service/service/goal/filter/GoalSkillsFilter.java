package school.faang.user_service.service.goal.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GoalSkillsFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getSkillIds() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto filters) {
        goals.removeIf(goal -> !getIdsSet(goal.getSkillsToAchieve()).containsAll(filters.getSkillIds()));
    }

    private Set<Long> getIdsSet(List<Skill> skills) {
        return new HashSet<>(skills.stream()
                .map(Skill::getId)
                .toList());
    }
}
