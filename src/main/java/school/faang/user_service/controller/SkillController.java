package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;


@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    private void validateSkill(SkillDto skill) {
        if (skill == null || skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("skill cannot be empty");
        }
    }
}
