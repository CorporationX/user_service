package school.faang.user_service.mapper.goal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.skill.Skill;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "skillsToAchieveIds", target = "skillsToAchieve", qualifiedByName = "mapSkillsIdsToSkills")
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveIds", qualifiedByName = "mapSkillsToSkillIds")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GoalDto toDto(Goal goal);

    @Mapping(source = "skillsToAchieveIds", target = "skillsToAchieve", qualifiedByName = "mapSkillsIdsToSkills")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGoalFromDto(UpdateGoalDto dto, @MappingTarget Goal entity);

    @Named(value = "mapSkillsIdsToSkills")
    default List<Skill> mapSkillsIdsToSkills(List<Long> skillsToAchieveIds) {
        if(skillsToAchieveIds == null) {
            return null;
        }
        return skillsToAchieveIds.stream()
                .map(skillId -> Skill.builder().id(skillId).build())
                .toList();
    }

    @Named(value = "mapSkillsToSkillIds")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        if(skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
