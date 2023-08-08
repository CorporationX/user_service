package school.faang.user_service.checker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.RecommendationPeriodIsNotCorrect;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Component
public class RecommendationChecker {
    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;

    private final RecommendationRepository recommendationRepository;

    public void check(RecommendationDto recommendation) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        ).filter(rec -> rec.getCreatedAt()

                .plusMonths(RECOMMENDATION_PERIOD_IN_MONTH)
                .isAfter(LocalDateTime.now()))
                .ifPresent(r -> {
                    throw new RecommendationPeriodIsNotCorrect(
                            "Date of new recommendation should be after "
                                    + RECOMMENDATION_PERIOD_IN_MONTH
                                    + " months of the last recommendation"
                    );
                });
    }
}
