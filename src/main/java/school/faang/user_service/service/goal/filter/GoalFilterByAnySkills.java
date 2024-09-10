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
    public List<Goal> applyFilter(List<Goal> goals, GoalFilterDto filters) {
        List<Long> requiredSkillIds = filters.getSkillIds();
        return goals.stream()
                .filter(goal -> isGoalGetAnySkills(goal, requiredSkillIds))
                .toList();
    }

    private boolean isGoalGetAnySkills(Goal goal, List<Long> skillIds) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .anyMatch(skillIds::contains);
    }
}
