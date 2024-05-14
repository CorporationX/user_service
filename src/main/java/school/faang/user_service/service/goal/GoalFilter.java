package school.faang.user_service.service.goal;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;


public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filterDto);

    void apply(List<Goal> goals, GoalFilterDto filter);
}
