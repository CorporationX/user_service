package school.faang.user_service.filters.goal;

import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDTO goalFilterDTO);

    List<Goal> apply(List<Goal> goals, GoalFilterDTO goalFilterDTO);
}
