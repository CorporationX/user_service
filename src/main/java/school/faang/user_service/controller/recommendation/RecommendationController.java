package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    public void giveRecommendation(RecommendationDto recommendation){
        validate(recommendation);
        recommendationService.create(recommendation);

    }

    private void validate(RecommendationDto recommendation){
        recommendationValidator.ValidateRecommendationContent(recommendation);
    }
}
