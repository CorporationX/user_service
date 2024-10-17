package school.faang.user_service.filter.goal;

import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters);
}
