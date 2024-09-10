package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillValidator serviceValidator;

    public List<Skill> getSkillsByTitle(List<String> skillsTitle) {
        serviceValidator.validateSkill(skillsTitle, skillRepository);
        return skillRepository.findByTitleIn(skillsTitle);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public void deleteSkillFromGoal(long goalId) {
        skillRepository.deleteAll(skillRepository.findSkillsByGoalId(goalId));
    }
}
