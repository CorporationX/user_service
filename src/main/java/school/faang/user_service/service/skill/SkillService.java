package school.faang.user_service.service.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill findSkillById(Long id) {
        Optional<Skill> skill = skillRepository.findById(id);
        return skill.orElse(null);
    }

    public List<Skill> findSkillsByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    public List<Skill> findSkillsByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    public List<Skill> findSkillsOfferedToUser(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId);
    }

    public Optional<Skill> findUserSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId);
    }

    public boolean skillExistsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public int countExistingSkills(List<Long> ids) {
        return skillRepository.countExisting(ids);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
}