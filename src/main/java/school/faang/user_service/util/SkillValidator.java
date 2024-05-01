package school.faang.user_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
public class SkillValidator {

    @Value("${skill.offers.min}")
    private long MIN_SKILL_OFFERS;
    private static final String EMPTY_TITLE_MSG = "Skill title can't be empty";

    public static void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank() || skill.getTitle().isEmpty()) {
            log.error(EMPTY_TITLE_MSG);
            throw new DataValidationException(EMPTY_TITLE_MSG);
        }
    }

    public void validateExistSkillByTitle(boolean checkResult, String title) {
        if (checkResult) {
            String error = "Skill with title: '" + title + "' already exists in DB";
            log.error(error);
            throw new DataValidationException(error);
        }
    }

    public void validateSkillPresent(boolean presentSkill, long skillId, long userId) {
        if (presentSkill) {
            throw new DataValidationException("User " + userId + " already has skill with ID: " + skillId);
        }
    }

    public void validateMinSkillOffers(int countOffersSkill, long skillId, long userId) {
        System.out.println("MIN_SKILL_OFFERS: " + MIN_SKILL_OFFERS);
        if (countOffersSkill < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Skill with ID: " + skillId + " hasn't enough offers for user with ID: " + userId);
        }
    }
}
