package school.faang.user_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;
@RequiredArgsConstructor
@Controller
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) throws IllegalAccessException {
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillDto> getOfferedSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) throws IllegalAccessException {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

}

