package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    public RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto getRecommendationRequest(long userId) {
        return recommendationRequestService.getRequest(userId);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}