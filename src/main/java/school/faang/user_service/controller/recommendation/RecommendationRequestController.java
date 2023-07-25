package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation_request.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto getRecommendationRequest(long id) {
       return recommendationRequestService.getRequest(id);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        return recommendationRequestService.getRecommendationRequests(filterDto);
    }
}
