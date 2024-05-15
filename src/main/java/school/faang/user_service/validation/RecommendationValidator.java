package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void recommendationEmptyValidation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().trim().isEmpty()) {
            throw new DataValidationException("Recommendation cannot be empty");
        }
    }

    public void recommendationTimeValidation(RecommendationDto recommendation) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(),
                recommendation.getReceiverId()).ifPresent(previousRecommendation -> {
            if (previousRecommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("Recommendation can be given after 6 months!");
            }
        });
    }

    public void skillEmptyValidation(RecommendationDto recommendation) {
        recommendation.getSkillOffers().forEach(skillOfferDto -> skillRepository.findById(skillOfferDto.getSkillId())
                .orElseThrow(() -> new DataValidationException("Skill with ID: " + skillOfferDto.getSkillId() + " not found")));
    }
}
