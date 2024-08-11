package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

public interface SkillService {

    SkillDto create(SkillDto skill);

    List<SkillDto> getUsersSkills(Long userId);

    List<SkillCandidateDto> getOfferedSkills(Long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);
}
