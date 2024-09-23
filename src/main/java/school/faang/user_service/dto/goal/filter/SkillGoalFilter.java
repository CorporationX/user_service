package school.faang.user_service.dto.goal.filter;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class SkillGoalFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.skillId() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> goal.getSkillsToAchieve().stream()
                .anyMatch(skill -> skill.getId() == filter.skillId()));
    }
}