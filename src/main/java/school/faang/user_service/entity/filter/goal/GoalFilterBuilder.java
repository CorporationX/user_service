package school.faang.user_service.entity.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.entity.filter.BaseFilterBuilder;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalFilterBuilder
        extends BaseFilterBuilder<Goal, FilterGoalDto>
        implements GoalFilterBuilderInterface<Goal, FilterGoalDto> {
    private final List<GoalFilterInterface> filters;

    @Override
    public List<GoalFilterInterface> getFilters() {
        return filters;
    }
}
