package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SkillValidator {
    public void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill must have a non-empty title.");
        }
    }
}
