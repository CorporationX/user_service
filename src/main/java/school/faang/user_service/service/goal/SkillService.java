package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean existsByTitle(List<Skill> skills)  {
        return skills.stream()
                .map(Skill::getTitle)
                .allMatch(skillRepository::existsByTitle);
    }

    public void create(List<Skill> skills, Long userId) {
        skills.stream()
                .map(Skill::getId)
                .forEach(skillId ->
                skillRepository.assignSkillToUser(skillId, userId));
    }

    public List<Skill> findSkillsByGoalId(Long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public void addSkillToUsers(List<User> users, Long goalId) {
        users.forEach(user ->
                user.getSkills()
                        .addAll(findSkillsByGoalId(goalId)));
    }

}
