package school.faang.user_service.service;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import java.util.List;

public interface SkillService {
    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);
}
