package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.ValidatorRecommendation;

@RestController
@RequiredArgsConstructor
public class RecommendationContoller {
    private final ValidatorRecommendation validatorRecommendation;
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validatorRecommendation.validateContent(recommendation);
        return recommendationService.create(recommendation);
    }
}
