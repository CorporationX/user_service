package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
public class RecommendationRequestController {
    private RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController (RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation (RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Recommendation request message should not be empty");
        } else {
            recommendationRequestService.create(recommendationRequest);
            return recommendationRequest;
        }
    }
}
