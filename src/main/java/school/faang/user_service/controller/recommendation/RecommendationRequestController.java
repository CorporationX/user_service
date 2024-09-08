package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation-requests")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null) {
            throw new DataValidationException("Recommendation request can not be null");
        }

        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("No filters were found");
        }

        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PatchMapping("/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank()) {
            throw new IllegalArgumentException("Rejection information is incorrect");
        }

        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
