package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    GoalDto toDto(Goal goal);

    Goal toEntity(GoalDto goalDto);

    void update(GoalDto dto, @MappingTarget Goal entity);

}
