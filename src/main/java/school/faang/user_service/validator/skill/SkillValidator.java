package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateSkill(SkillDto skill) throws IllegalAccessException {
        if (skill.title().isEmpty()) {
            throw new IllegalAccessException("Validation failed. Skill name is blank");
        }
        if (skillRepository.existsByTitle(skill.title())) {
            throw new IllegalAccessException("This skill already exists.");
        }
    }
}
