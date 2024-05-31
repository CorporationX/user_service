package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recommendation/request")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestValidator validator;

    @PostMapping
    public RecommendationRequestDto createRequestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping("/search")
    public List<RecommendationRequestDto> getRecommendationRequests(@Valid @RequestBody RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequestById(id);
    }

    @PutMapping("reject/{id}")
    public RecommendationRequestDto rejectRequest(
        @PathVariable
        Long id,
        @Valid
        @RequestBody
        RecommendationRequestRejectionDto rejection
    ) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
