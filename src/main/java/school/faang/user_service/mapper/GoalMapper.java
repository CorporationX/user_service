package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "toSkillIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Named("toSkillIds")
    default List<Long> toSkillIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream().map(Skill::getId).toList();
    }
}
