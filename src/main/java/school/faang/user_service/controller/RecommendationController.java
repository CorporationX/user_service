package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Component
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty.");
        }
        // Additional validation logic can be added here
    }
}
