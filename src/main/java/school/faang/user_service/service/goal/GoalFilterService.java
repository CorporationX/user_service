package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.filter.GoalFilter;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalFilterService {

    private final List<GoalFilter> goalFilters;

    public Stream<Goal> applyFilters(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.apply(goals, filter);
            }
        }
        return goals;
    }
}