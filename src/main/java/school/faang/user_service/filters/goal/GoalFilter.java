package school.faang.user_service.filters.goal;

import java.util.List;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {
  boolean isApplicable(GoalFilterDTO goalFilterDTO);

  List<Goal> apply(List<Goal> goals, GoalFilterDTO goalFilterDTO);
}
