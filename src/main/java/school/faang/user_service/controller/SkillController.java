package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validate.skill.SkillValidation;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillValidation skillValidation;

    public SkillDto create(SkillDto skill) {
        skillValidation.validateSkillTitle(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        skillValidation.validateNullUserId(userId);
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        skillValidation.validateNullUserId(userId);
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidation.validateNullSkillId(skillId);
        skillValidation.validateNullUserId(userId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}