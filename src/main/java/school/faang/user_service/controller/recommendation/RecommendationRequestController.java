package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new IllegalArgumentException("Recommendation request ca not be null");
        }

        recommendationRequestService.create(recommendationRequest);

        return null;
    }
}
