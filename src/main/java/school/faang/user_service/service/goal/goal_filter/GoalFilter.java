package school.faang.user_service.service.goal.goal_filter;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    Stream<Goal> apply(Stream<Goal> goal, GoalFilterDto filter);
}

