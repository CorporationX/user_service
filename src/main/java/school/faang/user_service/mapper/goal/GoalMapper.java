package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "skillsToIds")
    @Mapping(target = "parentId", source = "parent.id")
    GoalDto toDto(Goal goal);


    @Mapping(target = "skillsToAchieve", source = "skillIds", qualifiedByName = "idsToSkills")
    @Mapping(target = "parent.id", source = "parentId")
    Goal toEntity(GoalDto goal);

    List<GoalDto> goalListToDto(List<Goal> list);

    @Named("skillsToIds")
    default List<Long> mapSkillsToIds(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    @Named("idsToSkills")
    default List<Skill> mapIdsToSkills(List<Long> value) {
        List<Skill> res = new ArrayList<>(value.size());
        for (Long id : value) {
            Skill skill = new Skill();
            skill.setId(id);
            res.add(skill);
        }
        return res;
    }
}
