package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/skill")
    public SkillDto create (SkillDto skill) {
        validateSkill(skill.getTitle());

        return skillService.create(skill);
    }

    private void validateSkill (String skillTitle) {
        if (skillTitle == null || skillTitle.isBlank()) {
            throw new DataValidationException("Invalid skill name.");
        }
    }
}
