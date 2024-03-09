package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDto goalFilterDto);

    void apply(List<Goal> goals, GoalFilterDto goalFilterDto);
}
