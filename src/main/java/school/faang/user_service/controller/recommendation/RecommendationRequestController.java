package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Component
@Controller
@Slf4j
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RecommendationRejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
