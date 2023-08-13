package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private RecommendationRequestService recommendationRequestService;

    public List<RequestFilterDto> getRecommendationRequests(RequestFilterDto filterDto) {
        return recommendationRequestService.getRecommendationRequests(filterDto);
    }
}
