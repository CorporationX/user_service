package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper
public interface GoalMapper {
    Goal toGoal(CreateGoalDto createGoalDto);

    GoalDto toGoalDto(Goal goal);

    void update(UpdateGoalDto dto, @MappingTarget Goal entity);
}

