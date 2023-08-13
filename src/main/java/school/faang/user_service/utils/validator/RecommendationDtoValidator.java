package school.faang.user_service.utils.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class RecommendationDtoValidator {
    public void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new DataValidationException("Content is null or empty!");
        }
    }
}
