package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    public void giveRecommendation(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendation(recommendationDto);
        recommendationService.create(recommendationDto);
    }
}
