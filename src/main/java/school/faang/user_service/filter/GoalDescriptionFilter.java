package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@Component
public class GoalDescriptionFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter != null && filter.getDescriptionPattern() != null;
    }

    @Override
    public List<Goal> apply(GoalFilterDto filter, Stream<Goal> goals) {
        return goals.filter(goal -> goal.getDescription().contains(filter.getDescriptionPattern()))
                .toList();
    }
}
