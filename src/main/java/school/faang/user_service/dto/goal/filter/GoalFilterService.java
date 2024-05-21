package school.faang.user_service.dto.goal.filter;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilterService {

    Stream<Goal> applyFilters(Stream<Goal> goals, GoalFilterDto goalFilterDto);
}
