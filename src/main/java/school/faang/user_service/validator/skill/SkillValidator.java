package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    public void validateSkill(Skill skill, boolean existsByTitle) {
        if (skill.getTitle() == null) {
            throw new DataValidationException("skill title is required");
        }
        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Validation failed. Skill name is blank");
        }
        if (existsByTitle) {
            throw new DataValidationException("This skill already exists.");
        }
    }
}
