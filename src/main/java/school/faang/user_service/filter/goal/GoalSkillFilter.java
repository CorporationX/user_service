package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalSkillFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getSkillId() != null;
    }

    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goalContainSkillId(goal, goalFilterDto));
    }

    private boolean goalContainSkillId(Goal goal, GoalFilterDto goalFilterDto) {
        return goal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .anyMatch(id -> id.equals(goalFilterDto.getSkillId()));
    }
}
