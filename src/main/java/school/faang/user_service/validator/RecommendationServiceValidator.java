package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
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
            if (ChronoUnit.DAYS.between(recommendation.createdAt(), existingRecommendation.get().getCreatedAt())
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
}
