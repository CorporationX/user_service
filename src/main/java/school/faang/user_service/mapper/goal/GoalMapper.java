package school.faang.user_service.mapper.goal;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "String", injectionStrategy = InjectionStrategy.FIELD)
public interface GoalMapper {

    GoalDto toDto(Goal goal);

    Goal toEntity(GoalDto goalDto);
}
