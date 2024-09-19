package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillOfferValidation {
    private static final int MINIMUM_OFFERS = 3;

    public void validateOffers(List<SkillOffer> offers, Skill skill, User user) {
        if (offers.size() < MINIMUM_OFFERS) {
            throw new DataValidationException("The skill \"" + skill.getTitle() + "\" has been recommended to user " + user.getUsername() + " less than 3 times");
        }
    }
}
