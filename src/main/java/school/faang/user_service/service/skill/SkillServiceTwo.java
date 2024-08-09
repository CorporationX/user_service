package school.faang.user_service.service.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service("SkillServiceTwo")
public class SkillServiceTwo {

    @Autowired
    private SkillRepository skillRepository;

    public boolean existsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public int countExisting(List<Long> ids) {
        return skillRepository.countExisting(ids);
    }

    public List<Skill> findAllByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    public List<Skill> findSkillsOfferedToUser(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId);
    }

    public Optional<Skill> findUserSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }
}
