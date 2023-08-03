package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

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

    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validateRequest(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/get/requests")
    public List<RecommendationRequestDto> getRecommendationRequest(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(INVALID_ID);
        }
    }

    private void validateRequest(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new IllegalArgumentException(MESSAGE_EXCEPTION);
        }
    }

}
