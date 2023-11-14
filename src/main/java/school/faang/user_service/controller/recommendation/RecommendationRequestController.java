package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {

    private RecommendationRequestService recommendationRequestService;

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        return recommendationRequestService.getRecommendationRequests(filterDto);
    }
  
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
     
}
