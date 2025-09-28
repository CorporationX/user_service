package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {

    Goal toGoal(GoalDto goalDto);

    GoalDto toGoalDto(Goal goal);
}