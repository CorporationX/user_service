package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
public interface GoalFilter {
    boolean isApplicable(GoalFilterDto goalFilterDto);

    void apply(List<Goal> goals, GoalFilterDto goalFilterDto);
}
