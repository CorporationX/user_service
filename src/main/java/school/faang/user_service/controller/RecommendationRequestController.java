package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.RecommendationRequestResponseDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation-requests")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping
    public ResponseEntity<String> requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return ResponseEntity.ok(recommendationRequestService.requestRecommendation(recommendationRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestResponseDto> getById(@PathVariable(name = "id") Long recommendationId) {
        return ResponseEntity.ok(recommendationRequestService.getById(recommendationId));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectRequest(@PathVariable(name = "id") Long recommendationId,
                                                @RequestBody RejectionDto rejectionDto) {
        return ResponseEntity.ok(recommendationRequestService.rejectRequest(recommendationId, rejectionDto));
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecommendationRequestResponseDto>> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(recommendationRequestService.search(request));
    }

}
