package school.faang.user_service.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utils.validator.ValidatorController;

@RequiredArgsConstructor
@Controller

public class RecommendationController {

    private final RecommendationService recommendationService;
    private final ValidatorController validatorController;

    public void giveRecommendation(RecommendationDto recommendation) {
        validatorController.validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }

}
