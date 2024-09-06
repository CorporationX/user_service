package school.faang.user_service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = IGNORE)
public abstract class GoalMapper {

    @Autowired
    protected GoalRepository goalRepository;

    @Autowired
    protected SkillRepository skillRepository;

    @Mapping(target = "id", source = "goalId")
    @Mapping(target = "parent", source = "parentGoalId", qualifiedByName = "mapGoalIdToGoal")
    @Mapping(target = "skillsToAchieve", source = "skillIds", qualifiedByName = "mapSkillIdsToSkills")
    public abstract Goal toEntity(GoalDto dto);

    @Mapping(target = "goalId", source = "entity.id")
    @Mapping(target = "parentGoalId", source = "entity.parent.id")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToIds")
    public abstract GoalDto toDto(Goal entity);

    public abstract List<GoalDto> toDtos(List<Goal> entities);

    @Named("mapGoalIdToGoal")
    protected Goal mapGoalIdToGoal(Long id) {
        if (id == null) {
            return null;
        }
        return goalRepository.findById(id).orElse(null);
    }

    @Named("mapSkillIdsToSkills")
    protected List<Skill> mapSkillIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) {
            return null;
        }

        return skillRepository.findByIds(skillIds);
    }

    @Named("mapSkillsToIds")
    protected List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream()
            .map(Skill::getId)
            .toList();
    }
}
