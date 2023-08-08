package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@Tag(name = "Управление запросами на рекомендации")
@RestController
@RequestMapping("api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {
    public RecommendationRequestService recommendationRequestService;

    @Operation(summary = "Принять запрос на рекомендацию")
    @GetMapping("/users/{userId}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long userId) {
        return recommendationRequestService.getRequest(userId);
    }

    @Operation(summary = "Отклонить запрос на рекомендацию")
    @PostMapping("/reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}