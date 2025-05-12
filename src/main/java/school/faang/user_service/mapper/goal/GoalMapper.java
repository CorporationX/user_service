package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "skillIds", expression = "java(goalSkillsToSkillIDs(goal))")
    GoalDto toGoalDTO(Goal goal);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParent")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkills")
    Goal toGoal(GoalDto goalDto, @Context GoalMapperContext context);

    List<GoalDto> toGoalDTOs(List<Goal> goals);

    default List<Long> toSkillIds(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
    }

    void updateGoalFromDto(GoalDto dto, @MappingTarget Goal goal);

    @Named("mapParent")
    default Goal mapParent(Long id, @Context GoalMapperContext context) {
        return id != null ? context.toGoal(id) : null;
    }

    @Named("mapSkills")
    default List<Skill> mapSkills(List<Long> ids, @Context GoalMapperContext context) {
        return ids == null ? List.of() : context.toListSkills(ids);
    }
}
