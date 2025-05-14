package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "skillIds", expression = "java(toSkillIds(goal))")
    GoalDto toGoalDTO(Goal goal);

    List<GoalDto> toGoalDTOs(List<Goal> goals);

    default List<Long> toSkillIds(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
    }

    void updateGoalFromDto(GoalDto dto, @MappingTarget Goal goal);

}
