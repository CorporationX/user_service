package school.faang.user_service.controller.recomendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

@Component
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService) {
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null || recommendationRequestDto.getMessage().isBlank() ||
                recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message is blank or empty");
        }
        return recommendationRequestService.create(recommendationRequestDto);
    }
}
