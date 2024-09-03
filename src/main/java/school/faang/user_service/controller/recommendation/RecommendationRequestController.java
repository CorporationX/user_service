package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null) {
            throw new IllegalArgumentException("Recommendation request can not be null");
        }

        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter == null) {
            throw new IllegalArgumentException("No filters were found");
        }

        return recommendationRequestService.getRequests(filter);
    }
    
    public RecommendationRequestDto getRecommendationRequest(long id){
        return recommendationRequestService.getRequest(id);
    }
}
