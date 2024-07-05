package school.faang.user_service.service.skillService;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;
import java.util.Optional;

public interface SkillService {

    SkillDto create(SkillDto skill);

    List<SkillDto> getUsersSkills(Long userId);

    List<SkillCandidateDto> getOfferedSkills(Long userId);

    Optional<SkillDto> acquireSkillFromOffers(long skillId, long userId);
}
