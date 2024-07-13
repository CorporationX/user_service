package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        validateRequestNotEmpty(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RequestFilterDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequestsByFilter(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validateRequestNotEmpty(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage().isEmpty()) {
            throw new DataValidationException("Recommendation request message is empty");
        }
    }
}