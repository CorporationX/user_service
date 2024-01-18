package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Component
public class RecommendationRequestController {

    private RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (!(recommendationRequest.getMessage() != null) || !recommendationRequest.getMessage().isBlank() || !recommendationRequest.getMessage().isEmpty())
            throw new IllegalArgumentException("Incorrect user's message");
        return recommendationRequestService.create(recommendationRequest);
    }

}
