package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitlePattern() != null;
    }

    @Override
    public void apply(Stream<GoalDto> goals, GoalFilterDto filter) {
        goals.filter(goal -> goal.getTitle().contains(filter.getTitlePattern()));
    }
}
