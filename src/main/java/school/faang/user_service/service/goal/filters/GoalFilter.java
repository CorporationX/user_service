package school.faang.user_service.service.goal.filters;


import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filters);

    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters);

}
