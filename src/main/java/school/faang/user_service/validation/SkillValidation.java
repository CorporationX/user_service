package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;


@Component
@RequiredArgsConstructor
public class SkillValidation {

    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skill) {
        if (skill == null) {
            throw new DataValidationException("SkillDto can't be null");
        }
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Skill title can't be blank or null");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill \"" + skill.getTitle() + "\" already exist");
        }
    }

    public void checkUserSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User with id:" + userId + " already have skill with id" + skillId);
        }
    }

}
