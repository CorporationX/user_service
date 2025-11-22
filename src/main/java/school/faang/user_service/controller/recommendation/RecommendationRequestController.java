package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation-requests")
@Validated
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public ResponseEntity<RecommendationRequestDto> create(
            @Valid @RequestBody CreateRecommendationRequestDto recommendationDto) {
        return ResponseEntity.ok(recommendationRequestService.create(recommendationDto));
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecommendationRequestDto>> getByFilters(
            @Valid @RequestBody RecommendationRequestFilterDto filters) {
        return ResponseEntity.ok(recommendationRequestService.getByFilters(filters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(recommendationRequestService.getById(id));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable long id) {
        recommendationRequestService.accept(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable long id, @Valid @RequestBody RejectionDto rejection) {
        recommendationRequestService.reject(id, rejection);
    }
}
