package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    GoalDto toDto(Goal goal);

    Goal toEntity(GoalDto goalDto);
}
