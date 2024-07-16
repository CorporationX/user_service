package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    // @PostMapping("/recommendationRequest")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validateRequestNotEmpty(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

//    @PostMapping("/recommendationRequest")
//    public List<RequestFilterDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
//        return recommendationRequestService.getRequestsByFilter(filter);
//    }

//    @GetMapping("/recommendationRequest/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

//    @PostMapping("/recommendationRequest/{id}")
    public RejectionDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        if (rejection == null) {
            throw new DataValidationException("");
        }
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validateRequestNotEmpty(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage().isEmpty()) {
            throw new DataValidationException("Recommendation request message is empty");
        }
    }
}