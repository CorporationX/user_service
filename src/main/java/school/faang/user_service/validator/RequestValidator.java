package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.RecommendationRequestDto;

@Component
public class RequestValidator {
    public void validateRecomendationRequest (RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null || recommendationRequestDto.getMessage().isBlank() ||
                recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message is blank or empty");
        }
    }
}
