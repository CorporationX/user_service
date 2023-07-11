package school.faang.user_service.contrtoller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@RequiredArgsConstructor
@RestController
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public static final String INVALID_ID = "Recommendation request id must be greater than 0";

    public RecommendationRequestDto getRecommendationRequest(long id) {
        validateId(id);
        return recommendationRequestService.getRequest(id);
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(INVALID_ID);
        }
    }
}
