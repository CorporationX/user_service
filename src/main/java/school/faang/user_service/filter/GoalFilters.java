package school.faang.user_service.filter;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilters {

    boolean isApplicable(GoalFilterDto filters);

    void apply(Stream<Goal> goals, GoalFilterDto filters);
}
