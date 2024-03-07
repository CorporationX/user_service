package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "toSkillIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "goal.skillsToAchieve", ignore = true)
    @Mapping(target = "goal.parent", ignore = true)
    @Mapping(target = "id", source = "goal.id")
    @Mapping(target = "title", source = "goalDto.title")
    @Mapping(target = "description", source = "goalDto.description")
    @Mapping(target = "status", source = "goalDto.status")
    Goal updateGoal(Goal goal, GoalDto goalDto);

    @Named("toSkillIds")
    default List<Long> toSkillIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream().map(Skill::getId).toList();
    }
}
