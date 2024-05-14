package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.getTitle() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto filter) {
        goals.removeIf(g -> !g.getTitle().contains(filter.getTitle()));
    }
}

