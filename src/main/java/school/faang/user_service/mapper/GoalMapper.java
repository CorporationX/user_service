package school.faang.user_service.mapper;

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
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkills")
    GoalDto toDto(Goal entity);

    @Mapping(source = "parentId", target = "parent.id")
    Goal toEntity(GoalDto dto);

    @Named("mapSkills")
    default List<Long> mapSkills(List<Skill> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream().map(Skill::getId).toList();
    }
}
