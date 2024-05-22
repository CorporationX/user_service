package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillValidator {

    @Value("${skill.offers.min}")
    private long MIN_SKILL_OFFERS;
    private static final String EMPTY_TITLE_MSG = "Skill title can't be empty";

    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank()) {
            throw new DataValidationException(EMPTY_TITLE_MSG);
        }
    }

    public void validateExistSkillByTitle(String title) {
        boolean checkResult = skillRepository.existsByTitle(title);
        if (checkResult) {
            String error = "Skill with title: '" + title + "' already exists in DB";
            log.error(error);
            throw new DataValidationException(error);
        }
    }

    public void validateSkillPresent(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            throw new DataValidationException("User " + userId + " already has skill with ID: " + skillId);
        }
    }

    public void validateMinSkillOffers(int countOffersSkill, long skillId, long userId) {
        if (countOffersSkill < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Skill with ID: " + skillId + " hasn't enough offers for user with ID: " + userId);
        }
    }
}
