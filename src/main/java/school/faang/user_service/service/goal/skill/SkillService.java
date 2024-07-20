package school.faang.user_service.service.goal.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> findAllById(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    public List<Skill> findSkillsByGoalId(Long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public void assignSkillToUser(Long skillId, Long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
