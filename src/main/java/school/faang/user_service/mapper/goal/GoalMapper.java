package school.faang.user_service.mapper.goal;

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
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillToSkillsId")
    GoalDto toGoalDto(Goal goal);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapIdToParent")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkillIdToListSkill")
    Goal toGoal(GoalDto goalDto);

    @Named("mapSkillToSkillsId")
    default List<Long> mapSkillToSkillsId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }

    @Named("mapIdToParent")
    default Goal mapIdToParent(Long id) {
        Goal goal = new Goal();
        goal.setId(id);

        return goal;
    }

    @Named("mapSkillIdToListSkill")
    default List<Skill> mapSkillIdToListSkill(List<Long> skillId) {
        return skillId.stream().map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
        }).toList();
    }
}
