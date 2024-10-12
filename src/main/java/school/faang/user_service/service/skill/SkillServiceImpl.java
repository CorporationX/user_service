package school.faang.user_service.service.skill;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Transactional
    public void assignSkillsToUser(User user, List<Skill> skills) {
        for (Skill skill : skills) {
            skillRepository.assignSkillToUser(skill.getId(), user.getId());
        }
    }

    public void removeSkillsFromUser(User user, List<Skill> skills) {
        for (Skill skill : skills) {
            skillRepository.findUserSkill(skill.getId(), user.getId())
                    .ifPresent(skillRepository::delete);
        }
    }

    public void updateSkills(User user, long goalId) {
        List<Skill> oldSkills = user.getSkills();
        List<Skill> newSkills = skillRepository.findSkillsByGoalId(goalId);
        removeSkillsFromUser(user, oldSkills);
        assignSkillsToUser(user, newSkills);
    }
}