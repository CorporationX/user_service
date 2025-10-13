package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Skill;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SkillGoalFilter implements FilterGoal {
    @Override
    public boolean isApplication(GoalFilterDto goalFilterDto) {
        return goalFilterDto.skillIds() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        Set<Long> requiredSkillIds = new HashSet<>(goalFilterDto.skillIds());

        return goals
                .filter(goal -> hasAllSkills(goal, requiredSkillIds));
    }

    private boolean hasAllSkills(Goal goal, Set<Long> requiredSkillIds) {
        return goal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toSet())
                .containsAll(requiredSkillIds);
    }
}
