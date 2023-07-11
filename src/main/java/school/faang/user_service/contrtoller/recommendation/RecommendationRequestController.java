package school.faang.user_service.contrtoller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@RequiredArgsConstructor
@RestController
public class RecommendationRequestController {
    private final static String MESSAGE_EXCEPTION = "Message is blank or null";

    private final RecommendationRequestService service;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        validate(recommendationRequest);
    }

    private void validate(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage().isBlank() || recommendationRequest.getMessage() == null) {
            throw new IllegalArgumentException(MESSAGE_EXCEPTION);
        }
    }
}
