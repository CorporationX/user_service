package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(requestService.getRequest(id)).getBody();
    }

    @GetMapping(ALL_REQUESTS_FILTER)
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return ResponseEntity.ok(requestService.getRequests(filter)).getBody();
    }

    @PostMapping(REQUEST)
    public RecommendationRequestDto rejectRequest(@RequestParam @Positive long recommendationRequestId,
                                                                  @RequestParam @NotBlank @NotNull String reasonReject) {
        return ResponseEntity.ok(requestService.rejectRequest(recommendationRequestId, reasonReject)).getBody();
    }
}
