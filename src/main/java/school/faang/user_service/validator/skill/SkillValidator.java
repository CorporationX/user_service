package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    public void validateSkill(Skill skill,boolean existsByTitle) throws DataValidationException {
        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Validation failed. Skill name is blank");
        }
        if (existsByTitle) {
            throw new DataValidationException("This skill already exists.");
        }
    }
}
