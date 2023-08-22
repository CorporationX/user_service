package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/request/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new DataValidationException("Recommendation request message should not be empty");
        } else {
            return recommendationRequestService.create(recommendationRequest);
        }
    }

    @PostMapping("/request")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/request")
    public RecommendationRequestDto getRecommendationRequest(@RequestParam long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/request/{id}/reject")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isBlank()) {
            throw new DataValidationException("Recommendation request rejection reason should not be empty");
        } else {
            return recommendationRequestService.rejectRequest(id, rejection);
        }
    }
}
