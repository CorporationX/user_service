package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalStatusDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToSkillIds")
    @Mapping(source = "parent.id", target = "parentId")
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

    @Named("mapSkillsToSkillIds")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
