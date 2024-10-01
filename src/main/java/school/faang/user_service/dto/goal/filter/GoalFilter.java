package school.faang.user_service.dto.goal.filter;

import java.util.stream.Stream;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter);
}