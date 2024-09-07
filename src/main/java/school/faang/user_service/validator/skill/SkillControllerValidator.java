package school.faang.user_service.validator.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SkillControllerValidator {

    public void validateSkillByTitle(SkillDto skillDto) {
        if (skillDto.title() == null || skillDto.title().isEmpty()) {
            throw new DataValidationException("Skill title is empty");
        }
    }
}