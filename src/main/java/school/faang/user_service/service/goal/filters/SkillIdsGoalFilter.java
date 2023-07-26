package school.faang.user_service.service.goal.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.HashSet;
import java.util.stream.Stream;

@Component
public class SkillIdsGoalFilter implements GoalFilter {

    @Override
    public Stream<Goal> applyFilter(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> new HashSet<>(filters.getSkillIds()).containsAll(goal.getSkillsToAchieve().stream()
                .map(Skill::getId).toList()));
    }

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getSkillIds() != null && !filters.getSkillIds().isEmpty();
    }
}