package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    public RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto getRecommendationRequest(long userId) {
        return recommendationRequestService.getRequest(userId);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getFilterRequest(filter);
    }
}