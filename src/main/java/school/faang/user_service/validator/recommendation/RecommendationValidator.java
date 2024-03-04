package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.recommendations.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    public void checkRecommendation(RecommendationDto recommendation) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(), recommendation.getReceiverId())
                .filter(recommendationDate ->
                        recommendationDate.getCreatedAt()
                                .plusMonths(RECOMMENDATION_PERIOD_IN_MONTH)
                                .isAfter(LocalDateTime.now()))
                .ifPresent(message -> {
                    throw new DataValidationException(
                            "The recommendation can be given no earlier than " +
                                    RECOMMENDATION_PERIOD_IN_MONTH +
                                    " months after the previous one");
                });
    }

    public void existsSkillOffer(SkillOffer skillOffer) {
        if(!skillOfferRepository.existsById(skillOffer.getId())) {
            throw  new DataValidationException("...");
        }
    }

    public void validateUserHaveSkill(SkillOffer skillOffer, Recommendation recommendation) {
        if (!recommendation.getReceiver().getSkills().contains(skillOffer.getSkill())) {
            throw new DataValidationException("...");
        }
    }
}
