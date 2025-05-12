package school.faang.user_service.mapper.goal;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalMapperContext {
    Goal toGoal(long id);

    List<Skill> toListSkills(List<Long> skillIds);
}
