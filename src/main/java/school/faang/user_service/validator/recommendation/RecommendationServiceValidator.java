package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationServiceValidator {
    private static final int SIX_MONTH_IN_DAYS = 180;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateDaysBetweenRecommendations(RecommendationDto recommendation) {
        Optional<Recommendation> existingRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.authorId(),
                        recommendation.receiverId());

        if (existingRecommendation.isPresent()) {
            long between = ChronoUnit.DAYS.between(existingRecommendation.get().getCreatedAt(), recommendation.createdAt());
            if (between
                    < SIX_MONTH_IN_DAYS) {
                throw new DataValidationException("Need 180 days to pass before next recommendation");
            }
        }
    }

    public void validateSkillOffers(RecommendationDto recommendation) {
        boolean allSkillsExist = recommendation.skillOffers()
                .stream()
                .allMatch(skillOffer -> skillRepository.existsById(skillOffer.skillId()));

        if (!allSkillsExist) {
            throw new DataValidationException("Not all skills exist in database");
        }
    }

    public void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.content() == null || recommendation.content().isBlank()) {
            throw new DataValidationException("No content for recommendation");
        }

        if (recommendation.authorId() == null) {
            throw new DataValidationException("Author is missing in recommendation");
        }

        if (recommendation.receiverId() == null) {
            throw new DataValidationException("Receiver is missing in recommendation");
        }
    }
}
