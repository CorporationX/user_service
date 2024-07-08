package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationRequestController {
    public final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
