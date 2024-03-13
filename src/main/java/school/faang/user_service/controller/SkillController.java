package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validate.SkillValidate;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillValidate skillValidate;

    public SkillDto create(SkillDto skillDto) {
        skillValidate.validatorSkillsTitle(skillDto);
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long userid) {
        return skillService.getUserSkills(userid);
    }
    public List<SkillCandidateDto> getOfferedSkills(long userId){
        return skillService.getOfferedSkills(userId);
    }
    public SkillDto acquireSkillFromOffers(long skillId, long userId){
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}