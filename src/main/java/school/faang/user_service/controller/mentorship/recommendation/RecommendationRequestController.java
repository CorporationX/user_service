package school.faang.user_service.controller.mentorship.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation-request")
public class RecommendationRequestController {

    private final RecommendationRequestService requestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(
            @RequestBody RecommendationRequestDto recommendationRequest) {
        return requestService.create(recommendationRequest);
    }
}
