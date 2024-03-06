package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filters);

    void apply(List<Goal> goals, GoalFilterDto filters);
}
