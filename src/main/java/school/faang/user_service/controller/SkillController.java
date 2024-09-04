package school.faang.user_service.controller;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@Component
public class SkillController {
    private SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cannot is empty!");
        }
    }

}