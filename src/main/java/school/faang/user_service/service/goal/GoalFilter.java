package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

/**
 * @author Ilia Chuvatkin
 */

public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    void apply(Stream<Goal> goals, GoalFilterDto filter);
}
