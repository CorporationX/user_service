package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;

    public void validateSkillByTitle(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException("Skill with title " + skillDto.title() + " already exists");
        }
    }

    public void validateOfferedSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User " + userId + " already has skill with ID: " + skillId);
        }
    }

    public void validateSkillByMinSkillOffers(int skillOffers, long skillId, long userId) {
        if (skillOffers < MIN_SKILL_OFFERS) {
            throw new DataValidationException("User " + userId + "doesn't have enough skill offers to acquire the skill with ID: " + skillId);
        }
    }
    public void validateSkill(List<String> skills, SkillRepository skillRepository) {
        if (!skills.stream().allMatch(skillRepository::existsByTitle)) {
            throw new DataValidationException("Skills don't exist");
        }
    }
}