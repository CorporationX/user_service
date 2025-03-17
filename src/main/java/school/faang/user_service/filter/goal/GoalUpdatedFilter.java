package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalUpdatedFilter implements GoalFilter {
    @Override
    public boolean isApplicable(SearchGoalDto searchGoal) {
        return Objects.nonNull(searchGoal.updatedAt());
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoal) {
        if (!isApplicable(searchGoal)) {
            return goals;
        }
        return goals.filter(goal -> Objects.nonNull(goal.getUpdatedAt())
                && searchGoal.updatedAt().equals(goal.getUpdatedAt()));
    }
}
