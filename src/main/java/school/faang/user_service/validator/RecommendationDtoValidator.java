package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class RecommendationDtoValidator {

    private final RecommendationRepository recommendationRepository;
    private static final int MIN_MONTH_COUNT = 6;

    public void validateIfRecommendationContentIsBlank(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("Текст рекомендации не может быть пустым!");
        }
    }

    public void validateIfRecommendationCreatedTimeIsShort(RecommendationDto recommendation) {
        Recommendation existedRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(), recommendation.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Рекомендации нет в БД"));

        long month = ChronoUnit.MONTHS.between(existedRecommendation.getCreatedAt(), recommendation.getCreatedAt());

        if (month <= MIN_MONTH_COUNT) {
            throw new DataValidationException("Этот автор не может дать рекомендацию этому юзеру");
        }
    }
}
