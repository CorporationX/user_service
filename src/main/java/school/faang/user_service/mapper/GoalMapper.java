package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIdList")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Named("mapSkillsToIdList")
    default List<Long> mapSkillsToIdList(List<Skill> skillsToAchieve) {
        if (skillsToAchieve == null)
            return null;
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }
}
