package school.faang.user_service.mapper.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class GoalMapperContextImpl implements GoalMapperContext {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    @Override
    public Goal toGoal(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Goal id " + id));
    }

    @Override
    public List<Skill> toListSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
