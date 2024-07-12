package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private static final String ALL_REQUESTS_FILTER = "/requests";
    private final RecommendationRequestService requestService;

    @GetMapping(REQUEST)
    public RecommendationRequestDto getRecommendationRequest(Long id) {
        return requestService.getRequest(id);
    }

    @GetMapping(ALL_REQUESTS_FILTER)
    public List<RecommendationRequestDto> getRecommendationRequests(Long receiverId, RequestFilterDto filter) {
        return requestService.getRequests(receiverId, filter);
    }

    @PostMapping(REQUEST)
    public RecommendationRequestDto rejectRequest(@RequestParam Long recommendationRequestId, @RequestParam String reasonReject) {
        return requestService.rejectRequest(recommendationRequestId, reasonReject);
    }
}
