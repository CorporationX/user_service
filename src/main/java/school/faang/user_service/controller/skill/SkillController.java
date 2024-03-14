package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.skill.SkillValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final UserValidator userValidator;
    private final SkillService skillService;
    private final SkillValidator skillValidator;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validatorSkillsTitle(skillDto);
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long userid) {
        userValidator.validateUserExistsById(userid);
        return skillService.getUserSkills(userid);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userValidator.validateUserExistsById(userId);
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}