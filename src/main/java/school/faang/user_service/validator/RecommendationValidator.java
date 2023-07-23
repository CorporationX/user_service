package school.faang.user_service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;

@Component
public class RecommendationValidator {
    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;

    @Autowired
    private RecommendationRepository recommendationRepository;

    public void validate(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content should not be empty");
        }

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        ).map(rec -> rec.getCreatedAt()
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
