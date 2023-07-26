package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.service.reccomendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService service;

    public List<RecommendationRequestDto> getRecommendationRequest(RequestFilterDto filter) {
        return service.getRequest(filter);
    }
}
