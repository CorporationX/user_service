package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final RecommendationRepository recommendationRepository;
    private static final int MONTHS_LAST_RECOMMENDATION = 6;

    public void validateRecommendationContent(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("Content are empty or null ");
        }
    }

    public void validateRecommendationTerm(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("The author has not given a recommendation to this user"));

        LocalDateTime lastRecommendation = recommendation.getCreatedAt();
        LocalDateTime recommendationTerm = recommendationDto.getCreatedAt();

        if (recommendationTerm.isBefore(lastRecommendation.plusMonths(MONTHS_LAST_RECOMMENDATION))) {
            throw new DataValidationException("The author has already given a recommendation to this user within the last 6 months.");
        }
    }
}
