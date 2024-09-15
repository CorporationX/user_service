package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
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

    @GetMapping(REQUEST + "/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable("id") @Positive Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @GetMapping(FILTERED_REQUESTS)
    public List<RecommendationRequestDto> getRecommendationRequests(@NotNull RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @PostMapping(REQUEST)
    public RecommendationRequestDto rejectRequest(@Positive Long recommendationRequestId,
                                                  @RequestParam @NotBlank RejectionDto rejectionDto) throws DataValidationException {
        return recommendationRequestService.rejectRequest(recommendationRequestId, rejectionDto);
    }
}