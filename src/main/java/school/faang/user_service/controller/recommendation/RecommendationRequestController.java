package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null) {
            throw new IllegalArgumentException("Recommendation request can not be null");
        }

        return recommendationRequestService.create(recommendationRequestDto);
    }
}
