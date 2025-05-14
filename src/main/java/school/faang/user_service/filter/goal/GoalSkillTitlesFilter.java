package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalSkillTitlesFilter implements GoalFilter {

    @Override
    public boolean doFilter(Goal goal, GoalFilterDto criteria) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(criteria.getSkillTitles());
    }

    @Override
    public boolean isApplicable(GoalFilterDto criteria) {
        return criteria.getSkillTitles() != null;
    }
}
