package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillToAchieveIds")
    GoalDto toDto(Goal mentorshipRequest);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto mentorshipRequestDto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    void updateGoal(@MappingTarget Goal goal, GoalDto goalDto);

    default Long parentToId(Goal parent) {
        return parent != null ? parent.getId() : null;
    }

    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    GoalStatus toGoalStatus(String goalStatus);
}
