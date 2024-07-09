package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "map")
    GoalDto toGoalDto(Goal goal);

    Goal toGoal(GoalDto goalDto);

    default List<Long> map(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}