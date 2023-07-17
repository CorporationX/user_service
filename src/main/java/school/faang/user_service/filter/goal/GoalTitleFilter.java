package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Component
public class GoalTitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getTitle() != null;
    }

    @Override
    public void apply(List<Goal> goals, GoalFilterDto goalFilterDto) {
        goals.removeIf(goal -> !goal.getTitle().contains(goalFilterDto.getTitle()));
    }
}
