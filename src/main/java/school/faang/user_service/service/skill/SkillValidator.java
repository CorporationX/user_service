package school.faang.user_service.service.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SkillValidator {

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null) {
            throw new DataValidationException("Skill title is required");
        }

        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Skill with id " + skill.getId() + " is blank");
        }
    }
}
