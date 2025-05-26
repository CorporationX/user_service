package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(value = "/request")
    RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping(value = "/filter")
    List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping(value = "/{id}")
    RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping(value = "/{id}/reject")
    public RecommendationRequestDto rejectRequest(@PathVariable  Long id, @Valid @RequestBody RecommendationRequestRejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
