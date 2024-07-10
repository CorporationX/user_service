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
    private static final String ALL_REQUESTS_FILTER = "/requests-filter";
    private final RecommendationRequestService requestService;

    @GetMapping(REQUEST)
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(long id) {
        return ResponseEntity.ok(requestService.getRequest(id));
    }

    @GetMapping(ALL_REQUESTS_FILTER)
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(RequestFilterDto filter) {
        return ResponseEntity.ok(requestService.getRequests(filter));
    }

    @PostMapping(REQUEST)
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@RequestParam @Positive long recommendationRequestId,
                                                                  @RequestParam @NotBlank @NotNull String reasonReject) {
        return ResponseEntity.ok(requestService.rejectRequest(recommendationRequestId, reasonReject));
    }
}
