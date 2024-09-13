package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.Filter;

public interface GoalFilter extends Filter<GoalFilterDto, Goal> {}
