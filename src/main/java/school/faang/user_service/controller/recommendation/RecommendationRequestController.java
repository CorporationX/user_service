package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto getRecommendationRequest(long id) {
       return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
       return recommendationRequestService.rejectRequest(id, rejection);
    }
}
