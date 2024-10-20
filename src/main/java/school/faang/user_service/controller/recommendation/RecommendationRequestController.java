package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.dto.recommendation.RejectionDto;
import school.faang.user_service.model.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation-requests")
@Validated
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody @Valid @NotNull RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody @NotNull RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable @Positive long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable @NotNull @Positive long id, @RequestBody @Valid @NotNull RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
