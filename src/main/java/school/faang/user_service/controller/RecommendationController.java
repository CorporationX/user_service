package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;

@RestController
public class RecommendationController {
    @Autowired
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }
}
