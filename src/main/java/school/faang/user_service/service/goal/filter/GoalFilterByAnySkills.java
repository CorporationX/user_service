package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.Objects;

@Component
public class GoalFilterByAnySkills implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return Objects.nonNull(filters.getSkillIds());
    }

    @Override
    public boolean test(Goal goal, GoalFilterDto filters) {
        List<Long> requiredSkillIds = filters.getSkillIds();
        List<Long> goalSkillIds = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();

         return requiredSkillIds.stream()
                .allMatch(goalSkillIds::contains);
    }
}
