package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@Slf4j
@RestController
public class RecommendationRequestController {
    public final RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage().isEmpty()) {
            log.error(".getMessage() in requestRecommendation return empty value");
            throw new IllegalArgumentException("recommendation message can't be empty");
        }

        recommendationRequestService.create(recommendationRequestDto);
        return recommendationRequestDto;
    }
}
