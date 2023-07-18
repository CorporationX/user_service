package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        validateMessage(recommendationRequestDto);
        return recommendationRequestService.create(recommendationRequestDto);
    }

    private RecommendationRequestDto validateMessage(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage().isBlank() || recommendationRequestDto.getMessage() == null) {
            throw new DataValidationException("Message is empty!");
        }
        return recommendationRequestDto;
    }
}
