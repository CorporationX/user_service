package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation (RecommendationRequestDto recommendationRequest){
    validateRequestNotEmpty(recommendationRequest);
    recommendationRequestService.create(recommendationRequest);
    }

    private void validateRequestNotEmpty(RecommendationRequestDto recommendationRequest){
        if (recommendationRequest.getMessage().isEmpty()){
            throw new DataValidationException("Recommendation message is empty");
        }
    }
}
