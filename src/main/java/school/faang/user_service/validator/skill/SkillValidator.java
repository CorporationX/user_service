package school.faang.user_service.validator.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
public class SkillValidator {
    public void validateSkill(List<String> skills, SkillRepository skillRepository) {
        if (!skills.stream().allMatch(skillRepository::existsByTitle)) {
            throw new DataValidationException("Skills don't exist");
        }
    }
}
