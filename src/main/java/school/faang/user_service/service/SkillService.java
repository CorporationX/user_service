package school.faang.user_service.service;

import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;

import java.util.List;

public interface SkillService {

    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(Long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);

    List<Skill> getSkillsByIds(List<Long> skillIds);

    void assignSkillToUser(long skillId, long userId);

    void deleteSkillFromGoal(long goalId);
}
