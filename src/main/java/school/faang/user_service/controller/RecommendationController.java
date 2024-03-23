package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateContent(recommendation.getContent());
        return recommendationService.create(recommendation);
    }

    private void validateContent(String content) throws DataValidationException {
        if (content == null || content.isBlank()) {
            throw new DataValidationException(
                    "\"" + content + "\" is unavailable value for this field!");
        }
    }
}