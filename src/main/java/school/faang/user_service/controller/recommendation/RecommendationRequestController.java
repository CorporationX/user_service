package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private static final String REQUEST = "/request";
    private static final String FILTERED_REQUESTS = "/requests";
    private final RecommendationRequestService requestService;

    @PostMapping(REQUEST)
    public RecommendationRequestDto requestRecommendation(@RequestBody @NotNull RecommendationRequestDto recommendationRequestDto) {
        return requestService.create(recommendationRequestDto);
    }

    @GetMapping(REQUEST)
    public RecommendationRequestDto getRecommendationRequest(@Positive Long id) {
        return requestService.getRequest(id);
    }

    @GetMapping(FILTERED_REQUESTS)
    public List<RecommendationRequestDto> getRecommendationRequests(@Positive Long receiverId, @NotNull RequestFilterDto filter) {
        return requestService.getFilteredRequests(receiverId, filter);
    }

    @PostMapping(REQUEST + "/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable("id") @Positive Long recommendationRequestId,
                                                  @RequestParam @NotBlank String rejectionReason) {
        return requestService.rejectRequest(recommendationRequestId, rejectionReason);
    }
}
