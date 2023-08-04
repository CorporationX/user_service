package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.tasksEntity.invalidFieldException.EntityIsNullOrEmptyException;

@Component
public class RecommendationDtoValidator {
    public void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            throw new EntityIsNullOrEmptyException("Content is null or empty!");
        }
    }

}
