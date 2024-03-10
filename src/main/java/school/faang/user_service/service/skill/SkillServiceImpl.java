package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{
    private final SkillRepository skillRepository;

    public boolean checkActiveSkill(Long id) {
        return skillRepository.existsById(id);
    }

    public void saveAll(List<Skill> list) {
        skillRepository.saveAll(list);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
         return skillRepository.findSkillsByGoalId(goalId);
    }

    public void deleteAllSkills(List<Skill> skills) {
        skillRepository.deleteAll(skills);
    }
}
