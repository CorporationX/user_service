package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skill) throws IllegalArgumentException {
        if (skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Validation failed. Skill name is blank");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new IllegalArgumentException("This skill already exists.");
        }
    }
}
