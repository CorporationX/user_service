package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionRequestDto;
import school.faang.user_service.service.recommendation.request.RecommendationRequestService;

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

    @PutMapping("/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionRequestDto rejection) {
        return mRecommendationRequestService.rejectRequest(id, rejection);
    }
}
