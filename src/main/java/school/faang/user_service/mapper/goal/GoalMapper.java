package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    GoalDto toDto(Goal goal);

    Goal toEntity(GoalDto goalDto);

    List<GoalDto> toDto(List<Goal> goals);
}
