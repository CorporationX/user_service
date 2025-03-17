package school.faang.user_service.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {GoalMapperDecorator.class})
@DecoratedWith(GoalMapperDecorator.class)
public interface GoalMapper {
    @Mapping(target = "parentId", source = "parent", qualifiedByName = "mapParentToParentId")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToAchieveToSkillIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToParent")
    @Mapping(target = "skillsToAchieve", source = "skillIds", qualifiedByName = "mapSkillIdsToSkillsToAchieve")
    Goal toEntity(GoalDto goalDto);

    @Mapping(target = "id", ignore = true, source = "id")
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToParent")
    Goal update(@MappingTarget Goal goal, GoalDto goalDto);
}
