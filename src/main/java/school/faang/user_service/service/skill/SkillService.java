package school.faang.user_service.service.skill;

import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import java.util.List;

public interface SkillService {

    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(Long userId);

    List<SkillCandidateDto> getOfferedSkills(Long userId);

    SkillDto acquireSkillFromOffers(Long skillId, Long userId);

    List<Skill> findAllSkillsById(List<Long> skillIds);

    List<Skill> findSkillsByUserId(Long userId);

    List<Skill> findSkillsByGoalId(Long goalId);

    void saveAllSkills(List<Skill> skills);
}
