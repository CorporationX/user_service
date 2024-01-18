package school.faang.user_service.exception.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;

@Component
public class RecommendationValidator {
    public void validate(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty())
            throw new DataValidationException("Не заполнено поле рекомендации");
    }
}
