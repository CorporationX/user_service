package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
public class RecomendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecomendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping(value = "/give_recommendation")
    public Long giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }
}
