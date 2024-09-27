package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody @NotNull RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping("/request/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable("id") @Positive Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @GetMapping("/requests")
    public List<RecommendationRequestDto> getRecommendationRequests(@NotNull RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @PostMapping("/reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable("id") @Positive Long recommendationRequestId,
                                                  @RequestBody @NotNull RejectionDto rejectionDto) throws DataValidationException {
        return recommendationRequestService.rejectRequest(recommendationRequestId, rejectionDto);
    }
}
