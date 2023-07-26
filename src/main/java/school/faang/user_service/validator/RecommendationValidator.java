package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Component
public class RecommendationValidator {
    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;

    private final RecommendationRepository recommendationRepository;

    public void validate(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content should not be empty");
        }

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        ).filter(rec -> rec.getCreatedAt()
                .plusMonths(RECOMMENDATION_PERIOD_IN_MONTH)
                .isAfter(LocalDateTime.now()))
                .ifPresent(r -> {
                    throw new DataValidationException(
                            "Date of new recommendation should be after "
                                    + RECOMMENDATION_PERIOD_IN_MONTH
                                    + " months of the last recommendation"
                    );
                });
    }
}
