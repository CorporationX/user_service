package school.faang.user_service.contrtoller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendation")
public class RecommendationRequestController {
    private final static String MESSAGE_EXCEPTION = "Message is blank or null";

    private final RecommendationRequestService recommendationRequestService;

    public static final String INVALID_ID = "Id is required";

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        validateId(id);
        return recommendationRequestService.getRequest(id);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(INVALID_ID);
        }
    }


    @PostMapping("/request")
    public school.faang.user_service.dto.RecommendationRequestDto requestRecommendation(@RequestBody school.faang.user_service.dto.RecommendationRequestDto recommendationRequest) {
        validateRequest(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    private void validateRequest(school.faang.user_service.dto.RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new IllegalArgumentException(MESSAGE_EXCEPTION);
        }
    }

}
