package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

@Component
public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filter);
    void apply(List<GoalDto> goals, GoalFilterDto filter);
}
