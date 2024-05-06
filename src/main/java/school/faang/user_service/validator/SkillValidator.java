package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null) {
            throw new DataValidationException("title doesn't exist");
        } else if (skill.getTitle().isBlank()) {
            throw new DataValidationException("title is empty");
        } else if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(skill.getTitle() + " already exist");
        }
    }

    public void validateSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId) != null) {
            throw new DataValidationException("this skill with id " + skillId + " already exist");
        }
    }
}