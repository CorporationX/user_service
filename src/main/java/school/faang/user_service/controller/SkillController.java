package school.faang.user_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
public class SkillController {
    @Autowired
    private SkillService skillService;

    public SkillDto create(SkillDto skill) throws IllegalAccessException {
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) throws IllegalAccessException {
        if (!skill.getTitle().isEmpty()) {
            System.out.println("Validation succesfull");
        } else {
            System.out.println("Validation failed. Read about exception down below");
            throw new IllegalAccessException("Validation failed. Skill name is blank");
        }
        skillService.create(skill);

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

