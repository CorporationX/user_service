package school.faang.user_service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = IGNORE)
public interface GoalMapper {
    @Mapping(target = "goalId", source = "entity.id")
    @Mapping(target = "parentGoalId", source = "entity.parent.id")
    @Mapping(target = "skillIds", source = "skillsToAchieve")
    GoalDto toDto(Goal entity);

    List<GoalDto> toDto(List<Goal> entities);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
            .map(Skill::getId)
            .collect(Collectors.toList());
    }
}
