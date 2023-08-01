package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@RestController
@RequestMapping("api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {
    public RecommendationRequestService recommendationRequestService;

    @GetMapping("/users/{userId}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long userId) {
        return recommendationRequestService.getRequest(userId);
    }

    @PostMapping("/reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}