package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.GoalDto;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.Skill;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillToSkillsId")
    GoalDto toGoalDto(Goal goal);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapIdToParent")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkillIdToListSkill")
    Goal toGoal(GoalDto goalDto);

    @Named("mapSkillToSkillsId")
    default List<Long> mapSkillToSkillsId(List<Skill> skills) {
        return skills == null ? new ArrayList<>() : new ArrayList<>(skills.stream().map(Skill::getId).toList());
    }

    @Named("mapIdToParent")
    default Goal mapIdToParent(Long id) {
        Goal goal = new Goal();
        goal.setId(id);

        return goal;
    }

    @Named("mapSkillIdToListSkill")
    default List<Skill> mapSkillIdToListSkill(List<Long> skillId) {
        return skillId == null ? new ArrayList<>() : new ArrayList<>(skillId.stream().map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
        }).toList());
    }
}
