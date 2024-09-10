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

    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillEntityToSkillId")
    @Mapping(source = "parent.id", target = "parent")
    GoalDto toDto(Goal goal);

    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkillIdToSkillEntity")
    @Mapping(source = "parent", target = "parent", qualifiedByName = "mapParentIdToParent")
    Goal toEntity(GoalDto goalDto);

    List<GoalDto> toDtoList(List<Goal> subtasks);

    @Named("mapSkillEntityToSkillId")
    static List<Long> mapSkillEntityToSkillId(List<Skill> skills) {
       return skills.stream()
               .map(Skill::getId)
               .toList();
    }

    @Named("mapSkillIdToSkillEntity")
    static List<Skill> mapSkillIdToSkillEntity(List<Long> skillIds) {
        return skillIds.stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                })
                .toList();
    }

    @Named("mapParentIdToParent")
    static Goal mapParentIdToParent(Long parentId) {
        if (parentId == null) {
            return null;
        } else {
            Goal parentGoal = new Goal();
            parentGoal.setId(parentId);
            return parentGoal;
        }
    }


}
