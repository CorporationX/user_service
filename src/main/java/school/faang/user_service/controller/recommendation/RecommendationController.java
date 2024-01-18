package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    public void deleteRecommendation(long id) {
        recommendationService.deleteRecommendation(id);
    }

}
