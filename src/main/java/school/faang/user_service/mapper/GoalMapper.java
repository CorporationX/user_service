package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface GoalMapper {
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapToLongSkillId")
    GoalDto toDto(Goal goal);

    @Named("mapToLongSkillId")
    default List<Long> mapToLongSkillId(List<Skill> skills) {
        if (skills != null) {
            return skills.stream()
                    .map(Skill::getId)
                    .toList();
        }
        return Collections.emptyList();
    }
}