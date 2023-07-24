package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds")
    GoalDto toGoalDto(Goal goal);

    List<GoalDto> toDtoList(List<Goal> goals);

    default List<Long> skillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
