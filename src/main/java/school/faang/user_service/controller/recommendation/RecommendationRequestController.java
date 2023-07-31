package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/{id}/reject")
    public RecommendationRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        validateId(id);
        validateRejection(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validateRejection(RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is null or empty");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id is required");
        }
    }
}
