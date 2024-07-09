package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalDtoMapper {

    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "skillsToIds")
    @Mapping(source = "parent", target = "parentId", qualifiedByName = "goalParentToId")
    GoalDto toDto(Goal goal);

    List<GoalDto> toDtos(List<Goal> goals);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    List<Goal> toEntities(List<GoalDto> dtos);

    @Named("skillsToIds")
    static List<Long> toIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("goalParentToId")
    static Long goalParentToId(Goal goal) {
        return goal.getParent().getId();
    }
}
