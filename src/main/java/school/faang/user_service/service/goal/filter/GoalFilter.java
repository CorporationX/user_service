package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.function.Predicate;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filter);

    Predicate<Goal> apply(GoalFilterDto filter);
}
