package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private static final String REQUEST = "/request";
    private static final String FILTERED_REQUESTS = "/requests";
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(REQUEST)
    public RecommendationRequestDto requestRecommendation(@RequestBody @NotNull RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping(FILTERED_REQUESTS)
    public List<RecommendationRequestDto> getRecommendationRequests(@Positive Long receiverId, @NotNull RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getFilteredRecommendationRequest(receiverId, filter);
    }

    @GetMapping(REQUEST)
    public RecommendationRequestDto getRecommendationRequest(@Positive Long id) {
        return recommendationRequestService.getRecommendationRequest(id);
    }

    @PostMapping(REQUEST + "/{id}")
    public RecommendationRequestDto rejectRecommendationRequest(@PathVariable("id") @Positive Long recommendationRequestId,
                                                                @RequestParam @NotBlank String rejectionReason) throws DataValidationException {
        return recommendationRequestService.rejectRecommendationRequest(recommendationRequestId, rejectionReason);
    }
}