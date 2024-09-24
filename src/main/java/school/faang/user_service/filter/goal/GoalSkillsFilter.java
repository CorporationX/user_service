package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
class GoalSkillsFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getSkillIds() != null && !filters.getSkillIds().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .anyMatch(filter.getSkillIds()::contains)
        );
    }
}