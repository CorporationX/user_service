package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

public interface SkillService {
    void assignSkillsToUser(User user, List<Skill> skills);

    void removeSkillsFromUser(User user, List<Skill> skills);

    void updateSkills(User user, long goalId);
}