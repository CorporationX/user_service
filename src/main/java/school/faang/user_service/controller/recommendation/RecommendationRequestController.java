package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/recommendation/request")
public class RecommendationRequestController {

    private final RecommendationRequestService mRecommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        return mRecommendationRequestService.create(recommendationRequest);
    }

    @PostMapping("/list")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RecommendationRequestFilterDto filter) {
        return mRecommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return mRecommendationRequestService.getRecommendationRequest(id);
    }

    @PostMapping("/reject")
    public RecommendationRequestDto rejectRequest(@RequestBody RejectionDto rejection) {
        return mRecommendationRequestService.rejectRequest(rejection);
    }
}
