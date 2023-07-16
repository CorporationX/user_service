package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {
    Stream<Goal> applyFilter(Stream<Goal> goals, GoalFilterDto filters);
    boolean isApplicable(GoalFilterDto filterDto);
}
