package school.faang.user_service.mapper.goal;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "String", injectionStrategy = InjectionStrategy.FIELD)
public interface GoalMapper {

    @Mapping(target = "skillIds", source = "skillsToAchieve")
    GoalDto toDto(Goal goal);

    @Mapping(target = "skillsToAchieve", source = "skillIds")
    Goal toEntity(GoalDto goalDto);
}
