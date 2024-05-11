package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;

@RestController("recommendation/request")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestValidator validator;

    @PostMapping("create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validator.checkMessageIsBlank(recommendationRequest.getMessage());
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RecommendationRequestFilter filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequestById(id);
    }

    @PostMapping("reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        validator.checkMessageIsBlank(rejection.getReason());
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
