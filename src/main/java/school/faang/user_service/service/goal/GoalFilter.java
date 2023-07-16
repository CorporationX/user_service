package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;

import java.util.List;

public interface GoalFilter {
    List<GoalDto> applyFilter(List<GoalDto> goals);
    boolean isApplicable(GoalDto goalDto);
}
