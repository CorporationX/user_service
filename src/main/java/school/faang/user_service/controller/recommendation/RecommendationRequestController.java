package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private static final Logger log = LoggerFactory.getLogger(RecommendationRequestController.class);
    private final RecommendationRequestService requestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            log.error(
                    "In requestRecommendation method RecommendationRequestController class apply RecommendationRequestDto is null");
            throw new IllegalArgumentException(
                    "In requestRecommendation method RecommendationRequestController class apply RecommendationRequestDto is null");
        }
        return requestService.createRecommendationRequest(recommendationRequest);
    }

}
