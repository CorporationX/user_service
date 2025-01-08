package school.faang.user_service.service.skill;

import school.faang.user_service.entity.skill.Skill;

import java.util.List;

public interface SkillServiceInterface {

    List<Skill> getSKillsByIds(List<Long> skillIds);

    void addSkillsToUsersByGoalId(Long goalId);

}
