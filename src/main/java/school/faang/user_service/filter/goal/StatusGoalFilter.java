package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class StatusGoalFilter implements FilterGoal {
    @Override
    public boolean isApplication(GoalFilterDto goalFilterDto) {
        return goalFilterDto.status() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goal.getStatus() == goalFilterDto.status());
    }
}
