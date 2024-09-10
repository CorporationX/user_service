package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        Skill entity = skillMapper.toEntity(skill);
        Skill saved = skillService.createSkill(entity);
        return skillMapper.toDto(saved);
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cannot is empty!");
        }
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> userSkills = skillService.getUserSkills(userId);
        return skillMapper.toDtoList(userSkills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillCandidate = skillService.getOfferedSkills(userId);
        return skillMapper.toCandidateDtoList(skillCandidate);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toDto(skill);
    }
}