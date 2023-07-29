package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/rejectRequest/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
