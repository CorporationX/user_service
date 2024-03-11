package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    boolean checkActiveSkill(Long id);
    void saveAll(List<Skill> list);
    List<Skill> findSkillsByGoalId(long goalId);
    void deleteAllSkills(List<Skill> skills);
}
