package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Slf4j
public abstract class GoalMapperDecorator implements GoalMapper {
    @Autowired
    @Lazy
    @Qualifier("delegate")
    private GoalMapper delegate;

    @Autowired
    public GoalRepository goalRepository;

    @Autowired
    public SkillRepository skillRepository;

    @Override
    public GoalDto toDto(Goal goal) {
        return delegate.toDto(goal);
    }

    @Override
    public Goal toEntity(GoalDto goalDto) {
        return delegate.toEntity(goalDto);
    }

    @Override
    public Goal update(@MappingTarget Goal goal, GoalDto goalDto) {
        return delegate.update(goal, goalDto);
    }

    @Named("mapParentToParentId")
    public Long mapParentToParentId(Goal goal) {
        return goal.getId();
    }

    @Named("mapParentIdToParent")
    public Goal mapParentIdToParent(Long parentId) {
        return goalRepository.findById(parentId)
                .orElseThrow(() -> {
                            log.error("Goal parent not found by id {}", parentId);
                            return new GoalDataException("Goal parent not found by id " + parentId);
                        }
                );
    }

    @Named("mapSkillsToAchieveToSkillIds")
    public List<Long> mapSkillsToAchieveToSkillIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }

    @Named("mapSkillIdsToSkillsToAchieve")
    public List<Skill> mapSkillIdsToSkillsToAchieve(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
