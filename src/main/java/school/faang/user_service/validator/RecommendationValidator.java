package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private static final int RECOMMENDATION_INTERVAL_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;

    public void validateRecommendationExist(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository.findById(recommendationDto.getId())
                .orElse(null);

        if (recommendation == null) {
            throw new DataValidationException("Invalid recommendation to update");
        }
    }

    public void validateRecommendationContent(RecommendationDto recommendationDto) {
        String content = recommendationDto.getContent();

        if (content == null || content.isBlank()) {
            throw new DataValidationException("Content can't be empty");
        }
    }

    public void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long userId = recommendationDto.getReceiverId();
        Optional<Recommendation> lastRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime currentDate = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
                throw new DataValidationException(
                        "You've already recommended this user in the last " + RECOMMENDATION_INTERVAL_MONTHS + " months");
            }
        }
    }
}
