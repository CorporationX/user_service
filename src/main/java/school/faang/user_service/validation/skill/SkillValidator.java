package school.faang.user_service.validation.skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.message.ExceptionMessage;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillService skillService;

    public void validateSkill(SkillDto skillDto) {
        if (skillService.skillExistsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title: " + skillDto.getTitle() + " already exists.");
        }
    }

    public void validateSkillPresent(long skillId, long userId) {
        Optional<Skill> userSkill = skillService.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            throw new DataValidationException("User " + userId + " already has skill with ID: " + skillId);
        }
    }

    public void validateMinSkillOffers(long countOffersSkill, long skillId, long userId) {
        if (countOffersSkill < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Skill with ID: " + skillId + " hasn't enough offers for user with ID: " + userId);
        }
    }
}
