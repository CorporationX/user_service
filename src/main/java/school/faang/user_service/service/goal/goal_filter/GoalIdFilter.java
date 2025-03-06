package school.faang.user_service.service.goal.goal_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalIdFilter implements GoalFilter {


    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getId() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goal, GoalFilterDto filter) {
        return goal.filter(goal1 -> goal1.getId().equals(filter.getId()));
    }

}