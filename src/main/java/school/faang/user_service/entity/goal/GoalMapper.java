package school.faang.user_service.entity.goal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface GoalMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "goal.title", target = "title")
    @Mapping(source = "goal.description", target = "description")
    @Mapping(source = "goal.id", target = "id")
    @Mapping(source = "goal.status", target = "status")
    GoalDto toGoalDto(Goal goal);

    default List<Skill> mapSkillsToEntities(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return Collections.emptyList();  // Если список пуст, возвращаем пустой список
        }
        return skillIds.stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);  // Присваиваем ID скилла
                    return skill;
                })
                .collect(Collectors.toList());
    }
}
