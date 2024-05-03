package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.List;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitlePattern() != null;
    }

    @Override
    public void apply(List<GoalDto> goals, GoalFilterDto filter) {
        goals.removeIf(goal -> !goal.getTitle().contains(filter.getTitlePattern()));
    }
}
