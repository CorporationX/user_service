package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;


    public boolean validateSkill(Skill skill) {
        return skillRepository.existsByTitle(skill.getTitle());
    }

    public boolean validateSkillById(long id) {
        return skillRepository.existsById(id);
    }

    public void saveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public void saveSkills(List<Skill> skills) {
        skillRepository.saveAll(skills);
    }

    public void assignSkillToUser(long userId, long skillId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

}
