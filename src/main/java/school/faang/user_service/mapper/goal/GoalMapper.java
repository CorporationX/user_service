package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Named("skillsToIds")
    default List<Long> map(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "skillsToIds")
    @Mapping(target = "parentId", source = "parent.id")
    GoalDto toDto(Goal goal);

    List<GoalDto> goalListToDto(List<Goal> list);
}
