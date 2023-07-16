package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest){
        if (!recommendationRequest.getMessage().isEmpty() && recommendationRequest.getMessage() != null){
            recommendationRequestService.create(recommendationRequest);
            return recommendationRequest;
        } else {
            throw new IllegalArgumentException("Empty recommendation request!");
        }
    }
}
