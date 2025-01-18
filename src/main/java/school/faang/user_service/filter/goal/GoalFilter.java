package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public interface GoalFilter {

    boolean isApplicable(GoalFilterDto goalFilterDto);

    boolean apply(GoalFilterDto goalFilterDto, Goal goal);
}