package school.faang.user_service.utils.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

@Component
public class ValidatorController {
    public void validateRecommendation(RecommendationDto recommendation){
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("The recommendation you submitted is missing text");
        }
    }

}
