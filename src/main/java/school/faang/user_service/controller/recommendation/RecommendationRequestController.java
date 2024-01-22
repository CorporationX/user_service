package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.MessageRequestException;
import school.faang.user_service.service.RecommendationRequestService;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {

    private RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if ((recommendationRequest.getMessage() == null) || recommendationRequest.getMessage().isBlank() || recommendationRequest.getMessage().isEmpty())
            throw new MessageRequestException("Incorrect user's message");
        return recommendationRequestService.create(recommendationRequest);
    }

}
