package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapToSkillLong")
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

    @Named("mapToSkillLong")
    default List<Skill> mapToSkillLong(List<Long> skillId) {
        if (skillId != null) {
            return skillId.stream()
                    .map(id -> {
                        Skill skill = new Skill();
                        skill.setId(id);
                        return skill;
                    })
                    .toList();
        }
        return Collections.emptyList();
    }

    void updateFromDto (GoalDto dto, @MappingTarget Goal entity);
}