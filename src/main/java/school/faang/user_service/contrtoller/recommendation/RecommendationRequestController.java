package school.faang.user_service.contrtoller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@RequiredArgsConstructor
@RequestMapping("/recommendation")
@RestController
public class RecommendationRequestController {
    private final static String MESSAGE_EXCEPTION = "Message is blank or null";

    private final RecommendationRequestService service;


    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validateRequest(recommendationRequest);
        return service.create(recommendationRequest);
    }

    private void validateRequest(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new IllegalArgumentException(MESSAGE_EXCEPTION);
        }
    }
}
