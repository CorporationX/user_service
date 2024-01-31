package school.faang.user_service.filter;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    void apply(List<Goal> goals, GoalFilterDto filter);
}

