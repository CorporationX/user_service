package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Named("skillsToIds")
    default List<Long> map1(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "skillsToIds")
    @Mapping(target = "parentId", source = "parent.id")
    GoalDto toDto(Goal goal);


    @Mapping(target = "skillsToAchieve", source = "skillIds", ignore = true)
    @Mapping(target = "parent.id", source = "parentId")
    Goal toEntity(GoalDto goal);

    List<GoalDto> goalListToDto(List<Goal> list);
}
