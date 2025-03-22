package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalStatusDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "skillsToAchieve", target = "skillIds")
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "status", expression = "java(mapStatusToDto(goal.getStatus()))")
    GoalDto toDto(Goal goal);

    default GoalStatusDto mapStatusToDto(GoalStatus status) {
        if (status == null) {
            return null;
        }
        return GoalStatusDto.builder()
                .status(status.name())
                .build();
    }
}
