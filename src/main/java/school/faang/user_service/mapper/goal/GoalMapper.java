package school.faang.user_service.mapper.goal;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    final SkillRepository skillRepository = null;

    static List<Long> map(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    static long map(Goal value) {
        return value.getId();
    }

    @Mapping(target = "skillIds", source = "skillsToAchieve")
    @Mapping(target = "parentId", source = "parent")
    GoalDto toDto(Goal goal);

    static List<Skill> map2(Long value) {
        return skillRepository.findSkillsByGoalId(value);
    }

    @Mapping(target = "skillsToAchieve", source = "id")
    Goal toEntity(GoalDto goalDto);
}
