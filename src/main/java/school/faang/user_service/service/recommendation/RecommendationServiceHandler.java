package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecommendationServiceHandler {
    private static final int INTERVAL_IN_MONTHS = 6;

    public void selfRecommendationValidation(User author, User receiver) {
        if (author.getId().equals(receiver.getId())) {
            throw new DataValidationException("Cannot recommend yourself ");
        }
    }

    public void recommendationIntervalValidation(User author, User receiver) {
        author.getRecommendationsGiven()
                .stream()
                .filter(recommendation -> recommendation.getReceiver().getId().equals(receiver.getId()))
                .map(Recommendation::getCreatedAt)
                .min(LocalDateTime::compareTo)
                .ifPresent(this::intervalCheck);
    }

    private void intervalCheck(LocalDateTime localDateTime) {
        LocalDateTime seekInterval = LocalDateTime.now().minusMonths(INTERVAL_IN_MONTHS);
        if (localDateTime.isAfter(seekInterval)) {
            throw new DataValidationException("This receiver has already gave a recommendation less than " +
                    INTERVAL_IN_MONTHS + " months to that author."
            );
        }
    }

    public void skillOffersValidation(List<Long> skillOfferDtoIds, List<Long> allSkillsIds) {
        Set<Long> skillOfferDtoIdsSet = new HashSet<>(skillOfferDtoIds);
        Set<Long> allSkillsIdsSet = new HashSet<>(allSkillsIds);
        if (!allSkillsIdsSet.containsAll(skillOfferDtoIdsSet)) {
            throw new DataValidationException("SkillOffer of this recommendation not valid.");
        }
    }
}
