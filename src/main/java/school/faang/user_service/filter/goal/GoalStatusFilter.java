package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(SearchGoalDto searchGoal) {
        return Objects.nonNull(searchGoal.status());
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoal) {
        if (!isApplicable(searchGoal)) {
            return goals;
        }
        return goals.filter(goal -> searchGoal.status().equals(goal.getStatus()));
    }
}
