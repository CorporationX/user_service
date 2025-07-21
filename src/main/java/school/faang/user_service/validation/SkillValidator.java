package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.properties.SkillProperties;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillProperties skillProperties;

    public void validateSkillTitleIsUnique(boolean exists, String title) {
        if (exists) {
            throw new DataValidationException("Skill with title: " + title + " already exists.");
        }
    }

    public void ensureSkillExists(boolean exists, long skillId) {
        if (!exists) {
            throw new EntityNotFoundException("Skill with id " + skillId + " does not exist.");
        }
    }

    public void validateUserDoesNotHaveSkill(boolean userHasSkill, long skillId, long userId) {
        if (userHasSkill) {
            throw new ForbiddenException("User with id " + userId
                    + " already has skill with id " + skillId);
        }
    }

    public void validateEnoughSkillOffers(List<SkillOffer> offers) {
        long uniqueAuthorsCount = offers.stream()
                .map(offer -> offer.getRecommendation().getAuthor().getId())
                .distinct()
                .count();
        if (uniqueAuthorsCount < skillProperties.minOffersRequired()) {
            throw new ForbiddenException("Skill cannot be acquired. At least "
                    + skillProperties.minOffersRequired() + " unique users must offer this skill.");
        }
    }
}
