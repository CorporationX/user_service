package school.faang.user_service.service;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exceptions.DataValidationException;

import java.util.List;

public interface SkillService {
    List<SkillDto> getUserSkills(long userId);
    SkillDto create(Skill skill) throws DataValidationException;
    List<SkillCandidateDto> getOfferedSkills(long userId);
    SkillDto acquireSkillFromOffers(long skillId, long userId) throws DataValidationException;
}