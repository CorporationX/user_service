package school.faang.user_service.mapper.goal;

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
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillToSkillsId")
    GoalDto toGoalDto(Goal goal);

    Goal toGoal(GoalDto goalDto);

    @Named("mapSkillToSkillsId")
    default List<Long> mapSkillToSkillsId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
