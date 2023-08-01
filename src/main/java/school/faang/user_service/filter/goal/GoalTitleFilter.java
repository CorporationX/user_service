package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

@Component
public class GoalTitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.getTitle() != null;
    }

    @Override
    public void apply(List<GoalDto> list, GoalFilterDto filterDto) {
        list.removeIf((goalDto) -> !goalDto.getTitle().contains(filterDto.getTitle()));
    }
}
